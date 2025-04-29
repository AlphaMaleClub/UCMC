package com.alphamaleclub.ucmc.chat.controller;


import com.alphamaleclub.ucmc.chat.dto.ChatMessage;
import com.alphamaleclub.ucmc.chat.dto.ChatRoomRequest;
import com.alphamaleclub.ucmc.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    // 메시지 발행 및 저장
    @MessageMapping("/{chatRoomId}") // /chat주소로 발행된 메시지를
//    @SendTo({"/sub/{chatRoomId}", "/sub/chat-list"}) // sub/chat를 구독한 사용자에게 전달
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable Long chatRoomId) {
        log.info("채팅 도착 {}", chatMessage);
        log.info("채팅방 번호 {}", chatRoomId);

        switch (chatMessage.getMessageType()) {
            case JOIN:
                chatMessage.setContent(chatMessage.getSender() + "님이 입장하셨습니다.");
                chatService.joinChatRoom(chatMessage, chatRoomId);
                break;
            case LEAVE:
                chatMessage.setContent(chatMessage.getSender() + "님이 나갔습니다.");
                chatService.leaveChatRoom(chatMessage, chatRoomId);  // 이 부분이 누락되어 있었습니다
                break;
            case CHAT:
                chatService.addChatMessage(chatMessage, chatRoomId);
                break;
            default:
                log.warn("지원하지 않는 메시지 타입: {}", chatMessage.getMessageType());
        }
        simpMessagingTemplate.convertAndSend("/sub/" + chatRoomId, chatMessage);
        simpMessagingTemplate.convertAndSend("/sub/chat-list", chatMessage); // 채팅방 목록 업데이트용

        return chatMessage;
    }

    @PostMapping("/api/chatroom") //채팅방생성
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomRequest chatRoomRequest) {
        log.info("채팅방 생성 요청 {}", chatRoomRequest);
        Long roomId = chatService.createChatRoom(chatRoomRequest);
        return ResponseEntity.ok(roomId);
    }

    @GetMapping("/api/chatroom")
    public ResponseEntity<?> getChatRoomByMember(Authentication authentication) {
        return ResponseEntity.ok(chatService.getChatRoomListByMemberId(authentication));
    }

    // 채팅방 채팅 내역 출력
    @GetMapping("/api/chatmessage/{chatRoomId}")
    public ResponseEntity<?> getChatMessageByRoomId(@PathVariable Long chatRoomId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size
//                                                    @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable)

    ) {
        log.info("출력완료 {}", chatRoomId);
        log.info("page : {}", page);
        log.info("size : {}", size);
        Slice<ChatMessage> slice = chatService.getChatMessageByChatRoomId(chatRoomId,page,size);
        log.info("slice {}", slice);
        return ResponseEntity.ok(slice);
    }

}

