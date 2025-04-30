package com.alphamaleclub.ucmc.tradeBoard.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TradeStatus {

    ON_SALE,    // 판매 중
    SOLD_OUT,   // 판매 완료
    RESERVED    // 예약 중

}
