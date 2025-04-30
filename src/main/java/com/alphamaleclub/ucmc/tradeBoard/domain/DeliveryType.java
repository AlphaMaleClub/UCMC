package com.alphamaleclub.ucmc.tradeBoard.domain;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DeliveryType {

    //택배 거래
    PARCEL_DELIVERY,
    //직거래
    DIRECT_TRADE,
    //둘다
    BOTH;

}
