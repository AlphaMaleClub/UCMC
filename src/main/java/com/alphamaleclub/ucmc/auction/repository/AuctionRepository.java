package com.alphamaleclub.ucmc.auction.repository;

import com.alphamaleclub.ucmc.auction.domain.Auction;
import com.alphamaleclub.ucmc.auction.domain.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AuctionRepository extends JpaRepository<Auction, Long> {

    // 제목과 작성자 닉네임을 기준으로 경매글 검색
    @Query("SELECT a FROM Auction a " +
            "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "AND LOWER(a.member.nickname) LIKE LOWER(CONCAT('%', :nickname, '%'))")
    Page<Auction> searchAuctions(
            String title,
            String nickname,
            Pageable pageable
    );

    // 제목, 작성자 닉네임, 그리고 경매 상태 기준으로 경매글 검색
    @Query("SELECT a FROM Auction a " +
            "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "AND LOWER(a.member.nickname) LIKE LOWER(CONCAT('%', :nickname, '%')) " +
            "AND a.status = :status")
    Page<Auction> searchAuctionsByStatus(
            String title,
            String nickname,
            AuctionStatus status,
            Pageable pageable
    );

    // 상태 기준 검색
    Page<Auction> findByStatus(AuctionStatus status, Pageable pageable);
}
