package com.alphamaleclub.ucmc.auction.domain;

import com.alphamaleclub.ucmc.auction.dto.AuctionRequest;
import com.alphamaleclub.ucmc.auction.dto.AuctionResponse;
import com.alphamaleclub.ucmc.system.exception.auction.AuctionAlreadyFinishedException;
import com.alphamaleclub.ucmc.system.exception.auction.AuctionNotEditableException;
import com.alphamaleclub.ucmc.system.exception.auction.AuctionPriceTooLowException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDateTime endTime;
    private int price;
    private LocalDateTime createdAt;
    private String description;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    //추가로 이미지 받아와야함.

    // 입찰 발생 시 수정/삭제 불가
    private boolean hasBids = false;

    // 엔티티 생성 시점에 사용하는 스태틱 팩토리 메서드
    public static Auction of(String title,
                             String content,
                             LocalDateTime endTime,
                             int price,
                             String description) {
        Auction auction = new Auction();
        auction.title = title;
        auction.content = content;
        auction.endTime = endTime;
        auction.price = price;
        auction.description = description;
        auction.createdAt = LocalDateTime.now();
        auction.refreshStatus();
        return auction;
    }

    public static Auction from(AuctionRequest dto) {
        return Auction.of(
                dto.getTitle(),
                dto.getContent(),
                dto.getEndTime(),
                dto.getPrice(),
                dto.getDescription()
        );
    }

    // 상태 갱신 메서드
    // 종료 시간 도래시 FINISHED, 아니라면 ONGOING
    public void refreshStatus() {
        if (LocalDateTime.now().isAfter(endTime)) {
            this.status = AuctionStatus.FINISHED;
        } else {
            this.status = AuctionStatus.ONGOING;
        }
    }

    // 가격 변경 메서드
    public void placeBid(int bidPrice) {
        refreshStatus();
        if (this.status == AuctionStatus.FINISHED) {
            throw new AuctionAlreadyFinishedException("이미 경매가 종료되었습니다.");
        }
        if (bidPrice <= this.price) {
            throw new AuctionPriceTooLowException("입찰 금액이 현재 가격보다 낮습니다.");
        }

        this.price = bidPrice;
        this.hasBids = true;
    }

    // 경매글 수정 메서드
    public void updateAuction(String newTitle, String newContent, String newDescription) {
        if (this.hasBids) {
            throw new AuctionNotEditableException("이미 입찰자가 있으므로 수정이 불가능합니다.");
        }
        this.title = newTitle;
        this.content = newContent;
        this.description = newDescription;
    }

    // 경매글 삭제 가능 여부 검증
    public void validateDeletable() {
        if (this.hasBids) {
            throw new AuctionNotEditableException("이미 입찰자가 있으므로 삭제가 불가능합니다.");
        }
    }

    public AuctionResponse toDto() {
        return AuctionResponse.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .endTime(this.endTime)
                .price(this.price)
                .createdAt(this.createdAt)
                .description(this.description)
                .status(this.status)
                .build();
    }

}