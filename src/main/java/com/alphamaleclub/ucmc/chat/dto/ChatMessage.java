package com.alphamaleclub.ucmc.chat.dto;


import com.alphamaleclub.ucmc.chat.entity.Chat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessage {
    private String sender; // 채팅을 보낸 사람
    private String content; // 채팅 내용
    private MessageType messageType; // 메시지 타입
    private LocalDateTime timestamp; // 보낸 시간

    public static ChatMessage toEntity(Chat chat) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.sender = chat.getSender().getNickname();
        chatMessage.content = chat.getContent();
        chatMessage.messageType = MessageType.CHAT;
        chatMessage.timestamp = chat.getTimestamp();
        return chatMessage;
    }

    public enum MessageType {
        CHAT,JOIN,LEAVE
    }
}
