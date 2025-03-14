package com.alphamaleclub.ucmc.tradeBoard.dto;


import lombok.*;



@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanBoardMessageResponse {

    private String message;
    private boolean result;

}
