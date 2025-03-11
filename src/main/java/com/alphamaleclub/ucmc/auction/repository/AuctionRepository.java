package com.alphamaleclub.ucmc.auction.repository;

import com.alphamaleclub.ucmc.auction.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
