package com.alphamaleclub.ucmc.tradeBoard.contoroller;


import com.alphamaleclub.ucmc.tradeBoard.dto.CreateBoardRequest;
import com.alphamaleclub.ucmc.tradeBoard.service.impl.TradePostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradeBoardController {

    private final TradePostServiceImpl tradePostService;
    private CreateBoardRequest request;

    @PostMapping(value = "/board", consumes = "multipart/form-data")
    public ResponseEntity<String> createBoard(@RequestPart("data") CreateBoardRequest request, @RequestParam("images") List<MultipartFile> images) throws IOException {

        tradePostService.createTradePost(request, images);

        return ResponseEntity.ok("Board created");

    }

}
