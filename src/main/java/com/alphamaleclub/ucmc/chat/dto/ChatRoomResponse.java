package com.alphamaleclub.ucmc.chat.dto;


import com.alphamaleclub.ucmc.chat.entity.Chat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomResponse {
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    private Long chatRoomId;

    public ChatRoomResponse (Chat chat) {
        this.sender = chat.getSender().getNickname();
        this.content = chat.getContent();
        this.timestamp = chat.getTimestamp();
        this.chatRoomId = chat.getChatRoom().getId();
    }
}
