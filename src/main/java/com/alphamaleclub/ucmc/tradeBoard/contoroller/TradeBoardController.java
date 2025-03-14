package com.alphamaleclub.ucmc.tradeBoard.contoroller;


import com.alphamaleclub.ucmc.tradeBoard.domain.Status;
import com.alphamaleclub.ucmc.tradeBoard.dto.*;
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
    public ResponseEntity<KanbanBoardMessageResponse> createTradePost(@RequestPart("data") CreateTradeBoardRequest request, @RequestParam("images") List<MultipartFile> images) throws IOException {

        KanbanBoardMessageResponse result = tradePostService.createTradePost(request, images);

        return ResponseEntity.ok(result);

    }

    @GetMapping("/readPost/{postId}")
    public ResponseEntity<TradePostAndProductImageResponse> readTradePost(@PathVariable Long postId) {

        TradePostAndProductImageResponse result = tradePostService.getTradePost(postId);

        return ResponseEntity.ok(result);
    }


    @PutMapping(value = "/updatePost", consumes = "multipart/form-data")
    public ResponseEntity<KanbanBoardMessageResponse> updateTradePost(@RequestPart("data") UpdatePostRequest request, @RequestParam("images") List<MultipartFile> images) throws IOException {

        KanbanBoardMessageResponse result = tradePostService.updateTradePost(request, images);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping( "/deletePost")
    public ResponseEntity<KanbanBoardMessageResponse> deleteTradePost(@RequestBody DeleteTradePostRequest request) {

        KanbanBoardMessageResponse result = tradePostService.deleteTradePost(request);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/updatePostStatus/{postId}")
    public ResponseEntity<KanbanBoardMessageResponse> updateOnlyStatusTradePost(@PathVariable Long postId,Status status) {

        KanbanBoardMessageResponse result = tradePostService.updateOnlyStatusTradePost(postId,status);

        return ResponseEntity.ok(result);
    }






}
