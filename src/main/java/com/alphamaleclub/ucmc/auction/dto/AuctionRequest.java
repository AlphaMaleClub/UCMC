package com.alphamaleclub.ucmc.auction.dto;

import com.alphamaleclub.ucmc.auction.domain.Auction;
import com.alphamaleclub.ucmc.member.domain.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuctionRequest {

    private String title;
    private String content;
    private LocalDateTime endTime;
    private int price;
    private String description;

    public Auction toEntity(Member member) {
        return Auction.of(
                this.title,
                this.content,
                this.endTime,
                this.price,
                this.description,
                member
        );
    }

}
