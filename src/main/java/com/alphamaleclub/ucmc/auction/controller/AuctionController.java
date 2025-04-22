package com.alphamaleclub.ucmc.auction.controller;

import com.alphamaleclub.ucmc.auction.dto.AuctionImageResponse;
import com.alphamaleclub.ucmc.auction.dto.AuctionRequest;
import com.alphamaleclub.ucmc.auction.dto.AuctionResponse;
import com.alphamaleclub.ucmc.auction.service.AuctionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionServiceImpl auctionService;

    // 경매글 작성 (multipart/formdata 만 받는다는걸 명시함)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> create(@RequestPart("dto") AuctionRequest dto,
                                       @RequestPart("images") List<MultipartFile> imageFiles) throws IOException {

        Long auctionId = auctionService.createAuction(dto, imageFiles);
        return ResponseEntity.ok(auctionId);
    }

    // 경매글 목록 조회
    // title : 제목 검색
    // nickname : 작성자 검색
    // ongoingOnly : 경매중인 물품만 조회 옵션
    @GetMapping
    public ResponseEntity<Page<AuctionResponse>> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String nickname,
            @RequestParam(defaultValue = "false") boolean ongoingOnly,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<AuctionResponse> page = auctionService.getAuctionList(title, nickname, ongoingOnly, pageable);
        return ResponseEntity.ok(page);
    }

    // 경매글 조회
    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> get(@PathVariable Long id) {
        AuctionResponse dto = auctionService.getAuction(id);
        return ResponseEntity.ok(dto);
    }

    // 경매 입찰
    @PostMapping("/{id}/bid")
    public ResponseEntity<Void> bid(@PathVariable Long id, @RequestParam int bidPrice) {
        auctionService.bidAuction(id, bidPrice);
        return ResponseEntity.ok().build();
    }

    // 경매글 수정 (입찰 없을 때만 가능)
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody AuctionRequest dto) {
        auctionService.updateAuction(id, dto);
        return ResponseEntity.ok().build();
    }

    // 경매글 삭제 (입찰 없을 때만 가능)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auctionService.deleteAuction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<AuctionImageResponse>> getImages(@PathVariable Long id) {
        List<AuctionImageResponse> images = auctionService.getAuctionImages(id);
        return ResponseEntity.ok(images);
    }

    // 경매글 새 이미지 추가
    @PostMapping(path = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addImages(@PathVariable Long id,
                                          @RequestParam("images") List<MultipartFile> images) throws IOException {
        auctionService.addAuctionImages(id, images);
        return ResponseEntity.ok().build();
    }

    // 경매글 첨부 사진 수정: 수정할 이미지 ID와 새 파일을 매핑하여 받음
    @PatchMapping("/{id}/images")
    public ResponseEntity<Void> updateImages(@PathVariable Long id,
                                             @RequestParam Map<Long, MultipartFile> imagesToUpdate) throws IOException {
        auctionService.updateAuctionImages(id, imagesToUpdate);
        return ResponseEntity.ok().build();
    }

    // 경매글 첨부 사진 삭제: 삭제할 이미지 ID 리스트를 전달 (파라미터가 없으면 전체 삭제)
    @DeleteMapping("/{id}/images")
    public ResponseEntity<Void> deleteImages(@PathVariable Long id,
                                             @RequestParam(required = false) List<Long> imageIds) {
        auctionService.deleteAuctionImages(id, imageIds);
        return ResponseEntity.noContent().build();
    }
}
