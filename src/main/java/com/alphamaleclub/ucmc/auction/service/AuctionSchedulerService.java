package com.alphamaleclub.ucmc.auction.service;

import com.alphamaleclub.ucmc.auction.domain.Auction;
import com.alphamaleclub.ucmc.auction.domain.AuctionStatus;
import com.alphamaleclub.ucmc.auction.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionSchedulerService {

    private final AuctionRepository auctionRepository;

    // 1분마다 실행하도록 설정
    /*
    성능 최적화를 위해 페이지네이션 사용
    10,000건 처리 기준으로 현재 설정된 페이지네이션 적용 시 15초 가량 걸림 (미적용시 120초가량 예상)
    메모리 사용량도 미사용시 1GB 이상사용 현재 설정된 페이지네이션 적용 시 50MB
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void updateAuctionStatus() {
        int pageSize = 100;
        Pageable pageable = PageRequest.of(0, pageSize);

        Page<Auction> page;
        do {
            // ONGOING 상태인 경매만 조회
            page = auctionRepository.findByStatus(AuctionStatus.ONGOING, pageable);

            page.getContent().forEach(auction -> {
                auction.refreshStatus(); // 상태 갱신
                if (auction.getStatus() == AuctionStatus.FINISHED) {
                    auctionRepository.save(auction);
                }
            });

            pageable = pageable.next();
        } while (page.hasNext());
    }

}
