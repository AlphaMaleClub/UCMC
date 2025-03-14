package com.alphamaleclub.ucmc.tradeBoard.dto;

import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.tradeBoard.domain.Status;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
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
    String contents;
    Long price;
    Status status;
    String locate;
    LocalDateTime createdAt;
    String nickName;
    List<ProductImage> productImages;
}
