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
@CrossOrigin(origins = "http://localhost:3000")
public class TradeBoardController {

    private final TradePostServiceImpl tradePostService;

    @PostMapping(path = "/createPost", consumes = "multipart/form-data")
    public ResponseEntity<TradePostMessageResponse> createTradePost(@RequestPart("data") CreateTradeBoardRequest request, @RequestParam("images") List<MultipartFile> images) throws IOException {
        log.info("images = {}", images);
        TradePostMessageResponse result = tradePostService.createTradePost(request, images);

        return ResponseEntity.ok(result);

    }

    @GetMapping("/readAllPost")
    public ResponseEntity<GetAllTradePostAndImagesMessageResponse> readAllPost(@RequestParam  int page) {

        GetAllTradePostAndImagesMessageResponse result = tradePostService.getAllTradePost(page);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/readPost/{postId}")
    public ResponseEntity<TradePostAndProductImageResponse> readTradePost(@PathVariable Long postId) {

        TradePostAndProductImageResponse result = tradePostService.getTradePost(postId);

        return ResponseEntity.ok(result);
    }


    @PutMapping(value = "/updatePost", consumes = "multipart/form-data")
    public ResponseEntity<TradePostMessageResponse> updateTradePost(@RequestPart("data") UpdatePostRequest request, @RequestParam("images") List<MultipartFile> images) throws IOException {

        TradePostMessageResponse result = tradePostService.updateTradePost(request, images);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping( "/deletePost/{postId}")
    public ResponseEntity<TradePostMessageResponse> deleteTradePost(@PathVariable Long postId) {

        log.info("postId = {}", postId);
        TradePostMessageResponse result = tradePostService.deleteTradePost(postId);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/updatePostStatus/{postId}")
    public ResponseEntity<TradePostMessageResponse> updateOnlyStatusTradePost(@PathVariable Long postId, Status status) {

        TradePostMessageResponse result = tradePostService.updateOnlyStatusTradePost(postId,status);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/bumpPost/{postId}")
    public ResponseEntity<TradePostMessageResponse> BumpPost(@PathVariable Long postId) {

        TradePostMessageResponse result = tradePostService.updateOnlyUpdatedAt(postId);

        return ResponseEntity.ok(result);
    }





}
