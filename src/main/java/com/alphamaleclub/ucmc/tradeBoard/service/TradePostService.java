package com.alphamaleclub.ucmc.tradeBoard.service;

import com.alphamaleclub.ucmc.tradeBoard.domain.Status;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import com.alphamaleclub.ucmc.tradeBoard.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TradePostService {
    KanbanBoardMessageResponse createTradePost(CreateTradeBoardRequest request, List<MultipartFile> sourceImage) throws IOException;

    // member 파라미터로 받아서 따로 추가해주는 작업 해야함, principle 사용
    TradePost saveTradePost(CreateTradeBoardRequest request);

    KanbanBoardMessageResponse updateTradePost(UpdatePostRequest request, List<MultipartFile> sourceImage) throws IOException;

    KanbanBoardMessageResponse deleteTradePost(DeleteTradePostRequest request);

    TradePostAndProductImageResponse getTradePost(Long postId);

    KanbanBoardMessageResponse updateOnlyStatusTradePost(Long postId, Status status);
}
