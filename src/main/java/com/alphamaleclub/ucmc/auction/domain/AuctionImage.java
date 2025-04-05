package com.alphamaleclub.ucmc.auction.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auction_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // S3에서 반환된 이미지 URL
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @Builder
    public AuctionImage(String imageUrl, Auction auction) {
        this.imageUrl = imageUrl;
        this.auction = auction;
    }

    // 수정 시, 새로운 URL로 업데이트
    public void updateImageUrl(String newUrl) {
        this.imageUrl = newUrl;
    }

}