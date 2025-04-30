package com.alphamaleclub.ucmc.tradeBoard.domain;


import com.alphamaleclub.ucmc.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradePost {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status = TradeStatus.ON_SALE;

    @Column(nullable = false)
    private String locate;

    @Column(nullable = false)
    private String contents;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();


    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id", nullable = false)
    @JsonIgnore
    private Member member;

    @Column
    private DeliveryType deliveryType;

    @Column
    private Long bumpedCount = 0L;


    @Builder
    public TradePost (String title, Long price, String locate, String contents,
                         Member member,DeliveryType deliveryType) {
        this.title = title;
        this.price = price;
        this.locate = locate;
        this.contents = contents;
        this.member = member;
        this.deliveryType = deliveryType;

    }


    public void  updateTradePost (TradeStatus status, String title, Long price, String locate,
                                  String contents, DeliveryType deliveryType, Long dumpedCount, LocalDateTime updatedAt) {
        this.status = status;
        this.title = title;
        this.price = price;
        this.locate = locate;
        this.contents = contents;
        this.deliveryType = deliveryType;
        this.bumpedCount = dumpedCount;
        this.updatedAt = updatedAt;
    }






}
