package com.alphamaleclub.ucmc.tradeBoard.contoroller;


import com.alphamaleclub.ucmc.tradeBoard.dto.CreateBoardRequest;
import com.alphamaleclub.ucmc.tradeBoard.dto.UpdatePostRequest;
import com.alphamaleclub.ucmc.tradeBoard.service.impl.TradePostServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/Trade")
@RequiredArgsConstructor
public class TradeBoardController {

    private final TradePostServiceImpl tradePostService;

    @PostMapping(value = "/createPost", consumes = "multipart/form-data")
    public ResponseEntity<String> createTradePost(@RequestPart("data") CreateBoardRequest request, @RequestParam("images") List<MultipartFile> images) throws IOException {

        tradePostService.createTradePost(request, images);

        return ResponseEntity.ok("Created Trade Post");

    }

    @PostMapping(value = "/updatePost", consumes = "multipart/form-data")
    public ResponseEntity<String> updateTradePost(@RequestPart("data") UpdatePostRequest request, @RequestParam("images") List<MultipartFile> images) throws IOException {

        tradePostService.updateTradePost(request, images);

        return ResponseEntity.ok("Updated Trade Post");

    }




}
