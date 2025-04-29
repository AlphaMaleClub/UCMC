package com.alphamaleclub.ucmc.tradeBoard.dto;

import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.tradeBoard.domain.DeliveryType;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradeStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TradePostAndProductImageResponse {

    String title;
    String content;
    Long price;
    TradeStatus tradeStatus;
    String locate;
    LocalDateTime createdAt;
    String nickName;
    List<ProductImage> productImages;
    DeliveryType deliveryType;
    Long bumpedCount;
    Long memberId;

}
