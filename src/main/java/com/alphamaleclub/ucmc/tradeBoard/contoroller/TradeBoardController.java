package com.alphamaleclub.ucmc.tradeBoard.contoroller;



import com.alphamaleclub.ucmc.tradeBoard.anotation.AuthorOnly;
import com.alphamaleclub.ucmc.tradeBoard.dto.*;
import com.alphamaleclub.ucmc.tradeBoard.service.ProductImageService;
import com.alphamaleclub.ucmc.tradeBoard.service.TradePostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradeBoardController {

    private final TradePostService tradePostService;
    private final ProductImageService productImageService;

    @GetMapping(path = "/trade-posts/top10")
    public ResponseEntity<Top10PostResponse> getTop10Post() {

        log.info("백엔드 도착");
        Top10PostResponse result = tradePostService.findTop10();

        return ResponseEntity.ok(result);
    }
    //글 작성
    @PostMapping(path = "/trade-posts", consumes = "multipart/form-data")
    public ResponseEntity<TradePostMessageResponse> createTradePost(@RequestPart("data") CreateTradeBoardRequest request, @RequestPart("images") List<MultipartFile> images) throws IOException {

        TradePostMessageResponse result = tradePostService.createTradePost(request, images);

        return ResponseEntity.ok(result);

    }
    //전체 글 조회
    @GetMapping("/trade-posts")
    public ResponseEntity<GetAllTradePostAndImagesMessageResponse> readAllPost(
            @RequestParam int page,
            @RequestParam(defaultValue = "updatedAt,desc") String sort
    ) {
        GetAllTradePostAndImagesMessageResponse result = tradePostService.getAllTradePost(page, sort);
        return ResponseEntity.ok(result);
    }

    // 특정 글 조회
    @GetMapping("/trade-posts/{postId}")
    public ResponseEntity<TradePostAndProductImageResponse> readTradePost(@PathVariable Long postId) {

        TradePostAndProductImageResponse result = tradePostService.getTradePostAndImages(postId);

        return ResponseEntity.ok(result);
    }

    //글 수정
    @AuthorOnly
    @PutMapping(value = "/trade-posts/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<TradePostMessageResponse> updateTradePost(@PathVariable Long postId,@RequestPart("data") UpdatePostRequest request, @RequestPart("images") List<MultipartFile> images) throws IOException {
        log.info("request = {}", request);
        log.info("images = {}", images);

        TradePostMessageResponse result = tradePostService.updateTradePost(postId,request, images);


        return ResponseEntity.ok(result);
    }

    //글 삭제
    @AuthorOnly
    @DeleteMapping( "/trade-posts/{postId}")
    public ResponseEntity<TradePostMessageResponse> deleteTradePost(@PathVariable Long postId) {

        TradePostMessageResponse result = tradePostService.deleteTradePost(postId);

        return ResponseEntity.ok(result);
    }

    //status 변경
    @AuthorOnly
    @PutMapping("/trade-posts/{postId}/status")
    public ResponseEntity<TradePostMessageResponse> updateOnlyStatusTradePost(@PathVariable Long postId, @RequestBody StatusUpdateRequest status) {

        TradePostMessageResponse result = tradePostService.updateOnlyStatusTradePost(postId,status.getStatus());

        return ResponseEntity.ok(result);
    }

    //끌어올리기
    @AuthorOnly
    @PutMapping("/trade-posts/{postId}/bump")
    public ResponseEntity<TradePostMessageResponse> BumpPost(@PathVariable Long postId) {

        TradePostMessageResponse result = tradePostService.updateOnlyUpdatedAt(postId);

        return ResponseEntity.ok(result);
    }


    @GetMapping(path = "/trade-posts/{postId}/thumbnail")
    public ResponseEntity<GetTradePostImageResponse> getOnlyFirstPicture(@PathVariable Long postId) {

        GetTradePostImageResponse result = productImageService.getProductImageFirstByPostNumber(postId);

        return ResponseEntity.ok(result);
    }

    // 더미 파일 생성용
    @PostMapping(path = "/createDummyPost")
    public void createTradePost() {

        tradePostService.createDummyPost();

    }


}
