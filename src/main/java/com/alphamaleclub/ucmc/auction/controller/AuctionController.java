package com.alphamaleclub.ucmc.auction.controller;

import com.alphamaleclub.ucmc.auction.dto.AuctionRequest;
import com.alphamaleclub.ucmc.auction.dto.AuctionResponse;
import com.alphamaleclub.ucmc.auction.service.AuctionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionServiceImpl auctionService;

    // 경매글 작성
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody AuctionRequest dto) {
        Long auctionId = auctionService.createAuction(dto);
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
}
