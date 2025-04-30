package com.alphamaleclub.ucmc.tradeBoard.dto;


import com.alphamaleclub.ucmc.tradeBoard.domain.DeliveryType;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradeStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest {

    TradeStatus status;

    String title;

    String content;

    Long price;

    String locate;

    DeliveryType deliveryType;

    Long bumpedCount;

}
