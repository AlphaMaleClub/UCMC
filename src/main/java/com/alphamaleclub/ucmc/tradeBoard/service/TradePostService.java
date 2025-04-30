package com.alphamaleclub.ucmc.tradeBoard.service;

import com.alphamaleclub.ucmc.tradeBoard.domain.TradeStatus;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import com.alphamaleclub.ucmc.tradeBoard.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TradePostService {
    TradePostMessageResponse createTradePost(CreateTradeBoardRequest request, List<MultipartFile> sourceImage) throws IOException;

    // member 파라미터로 받아서 따로 추가해주는 작업 해야함, principle 사용
    TradePost saveTradePost(CreateTradeBoardRequest request);

    TradePostMessageResponse updateTradePost(Long postId,UpdatePostRequest request, List<MultipartFile> sourceImage) throws IOException;

    TradePostMessageResponse deleteTradePost(Long postId);

    Top10PostResponse findTop10();

    GetAllTradePostAndImagesMessageResponse getAllTradePost(int page, String srot);

    TradePost getTradePost(Long postId);

    TradePostAndProductImageResponse getTradePostAndImages(Long postId);

    TradePostMessageResponse updateOnlyStatusTradePost(Long postId, TradeStatus status);

    TradePostMessageResponse updateOnlyUpdatedAt(Long postId);

    TradePost getPostById(Long postId);

    void createDummyPost();
}
