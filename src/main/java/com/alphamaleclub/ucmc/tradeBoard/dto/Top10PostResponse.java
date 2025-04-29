package com.alphamaleclub.ucmc.tradeBoard.dto;

import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import java.util.List;

@Data
@Builder
public class Top10PostResponse {

    private String message;
    private boolean result;
    private List<TradePost> tradePosts;
    private List<ProductImageDto> images;

}
