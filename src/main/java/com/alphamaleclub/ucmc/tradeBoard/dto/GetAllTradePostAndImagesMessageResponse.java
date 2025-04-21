package com.alphamaleclub.ucmc.tradeBoard.dto;


import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;


@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAllTradePostAndImagesMessageResponse {

    private String message;
    private boolean result;
    private Page<TradePost> tradePosts;
    private List<ProductImageDto> images;
}
