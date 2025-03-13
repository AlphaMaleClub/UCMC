package com.alphamaleclub.ucmc.auction.repository;

import com.alphamaleclub.ucmc.auction.domain.Auction;
import com.alphamaleclub.ucmc.auction.domain.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    Page<Auction> findByTitleContainingIgnoreCaseAndMember_NicknameContainingIgnoreCase(
            String title,
            String nickname,
            Pageable pageable
    );

    Page<Auction> findByTitleContainingIgnoreCaseAndMember_NicknameContainingIgnoreCaseAndStatus(
            String title,
            String nickname,
            AuctionStatus status,
            Pageable pageable
    );
}
