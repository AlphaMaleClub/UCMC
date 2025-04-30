package com.alphamaleclub.ucmc.chat.service;



import com.alphamaleclub.ucmc.chat.dto.ChatMessage;
import com.alphamaleclub.ucmc.chat.dto.ChatRoomRequest;
import com.alphamaleclub.ucmc.chat.dto.ChatRoomResponse;
import com.alphamaleclub.ucmc.chat.entity.ChatRoom;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ChatService {
    public ChatRoom getChatRoomById(Long chatRoomId);
    public List<ChatRoomResponse> getChatRoomListByMemberId(Authentication authentication);
    public Slice<ChatMessage> getChatMessageByChatRoomId(Long chatRoomId, int page, int size);
    public void addChatMessage(ChatMessage chatMessage, Long chatRoomId);
    public Long createChatRoom(ChatRoomRequest chatRoomRequest);
    public void joinChatRoom(ChatMessage req, Long roomId);
    public void leaveChatRoom(ChatMessage req, Long roomId);
}
