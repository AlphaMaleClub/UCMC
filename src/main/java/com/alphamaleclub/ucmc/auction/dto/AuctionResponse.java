package com.alphamaleclub.ucmc.auction.dto;

import com.alphamaleclub.ucmc.auction.domain.AuctionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuctionResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime endTime;
    private final int price;
    private final LocalDateTime createdAt;
    private final String description;
    private final AuctionStatus status;

    private final String authorNickname;

    @Builder
    public AuctionResponse(Long id, String title, String content, LocalDateTime endTime, int price,
                              LocalDateTime createdAt, String description, AuctionStatus status, String authorNickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.endTime = endTime;
        this.price = price;
        this.createdAt = createdAt;
        this.description = description;
        this.status = status;
        this.authorNickname = authorNickname;
    }

}
