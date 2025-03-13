package com.alphamaleclub.ucmc.auction.service;

import com.alphamaleclub.ucmc.auction.domain.Auction;
import com.alphamaleclub.ucmc.auction.domain.AuctionStatus;
import com.alphamaleclub.ucmc.auction.dto.AuctionRequest;
import com.alphamaleclub.ucmc.auction.dto.AuctionResponse;
import com.alphamaleclub.ucmc.auction.repository.AuctionRepository;
import com.alphamaleclub.ucmc.system.exception.auction.AuctionDoesNotExistException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionServiceImpl {

    private final AuctionRepository auctionRepository;

    // 경매글 생성 시 dto에서 변환
    public Long createAuction(AuctionRequest dto) {
        Auction auction = Auction.from(dto);
        Auction saved = auctionRepository.save(auction);
        return saved.toDto().getId();
    }

    // 경매글 조회 시 dto로 반환
    public AuctionResponse getAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionDoesNotExistException("경매글이 존재하지 않습니다."));
        return auction.toDto();
    }

    // 입찰 진행
    public void bidAuction(Long auctionId, int bidPrice) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionDoesNotExistException("경매글이 존재하지 않습니다."));
        auction.placeBid(bidPrice);
    }

    // 경매글 수정 (입찰이 없을 때만 가능)
    public void updateAuction(Long auctionId, AuctionRequest dto) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionDoesNotExistException("경매글이 존재하지 않습니다."));
        auction.updateAuction(dto.getTitle(), dto.getContent(), dto.getDescription());
    }

    // 경매글 삭제 (입찰이 없을 때만 가능)
    public void deleteAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionDoesNotExistException("경매글이 존재하지 않습니다."));
        auction.validateDeletable();
        auctionRepository.delete(auction);
    }

    // 경매글 목록 (검색, 페이징, 정렬, 옵션)
    public Page<AuctionResponse> getAuctionList(String title, String nickname, boolean ongoingOnly, Pageable pageable) {
        // null 또는 빈 문자열 이스케이프
        String safeTitle = (title == null) ? "" : title.trim();
        String safeNickname = (nickname == null) ? "" : nickname.trim();

        Page<Auction> auctions;

        if (ongoingOnly) {
            // 경매 상태가 ONGOING 인 것만
            auctions = auctionRepository.findByTitleContainingIgnoreCaseAndMember_NicknameContainingIgnoreCaseAndStatus(
                    safeTitle,
                    safeNickname,
                    AuctionStatus.ONGOING,
                    pageable
            );
        } else {
            // 전체 상태
            auctions = auctionRepository.findByTitleContainingIgnoreCaseAndMember_NicknameContainingIgnoreCase(
                    safeTitle,
                    safeNickname,
                    pageable
            );
        }

        // Page<Auction> -> Page<AuctionResponse>
        return auctions.map(Auction::toDto);
    }

}
