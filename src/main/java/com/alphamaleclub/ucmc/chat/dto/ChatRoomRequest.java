package com.alphamaleclub.ucmc.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {
    private String memberName;
    private String opponentName;
}
