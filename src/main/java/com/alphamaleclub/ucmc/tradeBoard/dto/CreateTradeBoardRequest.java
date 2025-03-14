package com.alphamaleclub.ucmc.tradeBoard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTradeBoardRequest {

    String title;
    String content;
    Long price;
    String Locate;

}
