package com.alphamaleclub.ucmc.tradeBoard.dto;

import com.alphamaleclub.ucmc.tradeBoard.domain.DeliveryType;
import jakarta.persistence.Enumerated;
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
    @Enumerated
    DeliveryType deliveryType;


}
