package com.alphamaleclub.ucmc.auction.domain;

import com.alphamaleclub.ucmc.auction.dto.AuctionResponse;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.system.exception.auction.AuctionAlreadyFinishedException;
import com.alphamaleclub.ucmc.system.exception.auction.AuctionNotEditableException;
import com.alphamaleclub.ucmc.system.exception.auction.BiddingTooLowException;
import com.alphamaleclub.ucmc.system.exception.auction.InvalidStartPriceException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.alphamaleclub.ucmc.system.exception.ExceptionMessage.Auction.*;

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

    @Getter
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionImage> images = new ArrayList<>();

    // AuctionImage 리스트를 수정이 가능한 컬렉션으로 반환
    public List<AuctionImage> getModifiableImages() {
        return images;
    }

    // AuctionImage 추가
    public void addImage(AuctionImage image) {
        images.add(image);
    }

    // 특정 이미지 삭제
    public void removeImage(AuctionImage image) {
        images.remove(image);
    }

    // 입찰 발생 시 수정/삭제 불가
    private boolean hasBids = false;

    // 엔티티 생성 시점에 사용하는 스태틱 팩토리 메서드
    public static Auction of(String title,
                             String content,
                             LocalDateTime endTime,
                             int price,
                             String description,
                             Member member) {

        // 경매 시작가격 검증
        if (price <= 0) {
            throw new InvalidStartPriceException(INVALID_START_PRICE_EXCEPTION);
        }

        Auction auction = new Auction();
        auction.title = title;
        auction.content = content;
        auction.endTime = endTime;
        auction.price = price;
        auction.description = description;
        auction.createdAt = LocalDateTime.now();
        auction.member = member;
        auction.refreshStatus();
        return auction;
    }

//    public static Auction from(AuctionRequest dto) {
//        return Auction.of(
//                dto.getTitle(),
//                dto.getContent(),
//                dto.getEndTime(),
//                dto.getPrice(),
//                dto.getDescription()
//        );
//    }

    // 상태 갱신 메서드
    // 종료 시간 도래시 FINISHED, 아니라면 ONGOING
    public void refreshStatus() {
        if (LocalDateTime.now().isAfter(endTime)) {
            this.status = AuctionStatus.FINISHED;
        } else {
            this.status = AuctionStatus.ONGOING;
        }
    }

    // 경매종료 시간이 지난 경매건에 대한 상태변경처리용 메서드
    public void updateStatus(AuctionStatus newStatus) {
        if (this.status != newStatus) {
            this.status = newStatus;
        }
    }

    // 입찰용 가격 변경 메서드
    public void placeBid(int bidPrice) {
        refreshStatus();
        if (this.status == AuctionStatus.FINISHED) {
            throw new AuctionAlreadyFinishedException(AUCTION_ALREADY_FINISHED_EXCEPTION);
        }
        if (bidPrice <= this.price || bidPrice <= 0) {
            throw new BiddingTooLowException(BIDDING_TOO_LOW_EXCEPTION);
        }

        this.price = bidPrice;
        this.hasBids = true;
    }

    // 경매글 수정 메서드
    public void updateAuction(String newTitle, String newContent, String newDescription) {
        if (this.hasBids) {
            throw new AuctionNotEditableException(AUCTION_NOT_EDITABLE_EXCEPTION);
        }
        this.title = newTitle;
        this.content = newContent;
        this.description = newDescription;
    }

    // 경매글 삭제 가능 여부 검증
    public void validateEditableOrDeletable() {
        if (this.hasBids) {
            throw new AuctionNotEditableException(AUCTION_NOT_EDITABLE_EXCEPTION);
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
                .authorNickname(member.getNickname())
                .build();
    }

}