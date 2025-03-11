package com.alphamaleclub.ucmc.tradeBoard.service;

import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import com.alphamaleclub.ucmc.tradeBoard.dto.CreateBoardRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TradePostService {
    String createTradePost(CreateBoardRequest request, List<MultipartFile> sourceImage) throws IOException;

    // member 파라미터로 받아서 따로 추가해주는 작업 해야함, principle 사용
    TradePost saveTradePost(CreateBoardRequest request);
}
