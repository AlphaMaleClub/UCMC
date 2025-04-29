package com.alphamaleclub.ucmc.chat.service.impl;


import com.alphamaleclub.ucmc.chat.dto.ChatMessage;
import com.alphamaleclub.ucmc.chat.dto.ChatRoomRequest;
import com.alphamaleclub.ucmc.chat.dto.ChatRoomResponse;
import com.alphamaleclub.ucmc.chat.entity.Chat;
import com.alphamaleclub.ucmc.chat.entity.ChatRoom;
import com.alphamaleclub.ucmc.chat.entity.UserChatRoom;
import com.alphamaleclub.ucmc.chat.repository.ChatMessageRepository;
import com.alphamaleclub.ucmc.chat.repository.ChatRoomRepository;
import com.alphamaleclub.ucmc.chat.repository.UserChatRoomRepository;
import com.alphamaleclub.ucmc.chat.service.ChatService;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.services.MemberService;
import com.alphamaleclub.ucmc.system.exception.chat.ChatRoomNotFoundException;
import com.alphamaleclub.ucmc.system.exception.chat.InvalidChatRoomCreationException;
import com.alphamaleclub.ucmc.system.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.alphamaleclub.ucmc.system.exception.ExceptionMessage.Chat.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final MemberService memberService;
    private final SecurityUtil securityUtil;


    @Override
    public ChatRoom getChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(
                ()-> new ChatRoomNotFoundException(CHAT_ROOM_FOUND_ERROR)
        );
    }

    @Override
    public List<ChatRoomResponse> getChatRoomListByMemberId(Authentication authentication) {
//        MemberDetails memberDetails = memberService.getMemberDetails(authentication);
        Member loginMember = memberService.getLoginMember(authentication);
        List<ChatRoom> chatRooms = loginMember.getChatRooms();
        return chatRooms.stream()
                .filter(chatRoom -> chatRoom.getLastMessage() != null)
                .sorted((cr1, cr2) ->
                        cr2.getLastMessage().getTimestamp().compareTo(cr1.getLastMessage().getTimestamp())
                )
                .map(chatRoom -> new ChatRoomResponse(chatRoom.getLastMessage()))
                .collect(Collectors.toList());
    }

    @Override
    public Slice<ChatMessage> getChatMessageByChatRoomId(Long chatRoomId, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());

        Slice<Chat> byChatRoomId = chatMessageRepository.findByChatRoomIdOrderByTimestampDesc(chatRoomId, pageRequest);

        return byChatRoomId.map(ChatMessage::toEntity);
    }

//    @Override
//    public Page<ChatMessage> getChatMessageByChatRoomId(Long chatRoomId, int page, int size) {
//        Pageable pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
//        Page<Chat> byChatRoomId = chatMessageRepository.findByChatRoomId(chatRoomId, pageRequest);
//
//        return byChatRoomId.map(ChatMessage::toEntity);
//    }

    // 채팅 추가
    @Override
    @Transactional
    public void addChatMessage(ChatMessage chatMessage, Long roomId) {
        Member memberByNickname = memberService.getMemberByNickname(chatMessage.getSender());
        ChatRoom chatRoomById = getChatRoomById(roomId);
        Chat newChat = Chat.toEntity(chatMessage, memberByNickname, chatRoomById);
        chatRoomById.updateLastChat(newChat);
        chatMessageRepository.save(newChat);
    }

    // 채팅방 생성
    @Override
    @Transactional
    public Long createChatRoom(ChatRoomRequest chatRoomRequest) {
        if (Objects.equals(chatRoomRequest.getMemberName(), chatRoomRequest.getOpponentName())) {
            throw new InvalidChatRoomCreationException(INVALID_CHAT_ROOM_CREATION_ERROR);
        }
        Member member = memberService.getMemberByNickname(chatRoomRequest.getMemberName());
        Member opponent = memberService.getMemberByNickname(chatRoomRequest.getOpponentName());

        Optional<Long> existRoom = userChatRoomRepository.findChatRoomIdsByMembers(member, opponent);

        // 만약 이미 채팅방이 생성되었있다면 해당 채팅방을 return
        if(existRoom.isPresent()) {
            return existRoom.get();
        }

        ChatRoom newChatRoom = new ChatRoom();
        chatRoomRepository.save(newChatRoom);

        userChatRoomRepository.save(UserChatRoom.toEntity(member, newChatRoom));
        userChatRoomRepository.save(UserChatRoom.toEntity(opponent, newChatRoom));
        return newChatRoom.getId();
    }


    // 멤버 추가
    @Override
    @Transactional
    public void joinChatRoom(ChatMessage req, Long roomId) {
        ChatRoom joinRoom = getChatRoomById(roomId);
        Member joinMember = memberService.getMemberByNickname(req.getSender());
        // 이미 참여 중인지 확인
        boolean alreadyJoined = userChatRoomRepository.existsByMemberAndChatRoom(joinMember, joinRoom);
        if (!alreadyJoined) {
            userChatRoomRepository.save(UserChatRoom.toEntity(joinMember, joinRoom));
        }
    }

    // 채팅방 나가기
    @Override
    @Transactional
    public void leaveChatRoom(ChatMessage req, Long roomId) {
        ChatRoom leaveChatRoom = getChatRoomById(roomId);
        Member leaveMember = memberService.getMemberByNickname(req.getSender());
        // 이미 참여 중인지 확인
        UserChatRoom userChatRoom = userChatRoomRepository.findByMemberAndChatRoom(leaveMember, leaveChatRoom)
                .orElseThrow(() -> new RuntimeException(USER_NOT_IN_CHAT_ROOM_ERROR));

        userChatRoomRepository.delete(userChatRoom);

    }


}
