package com.alphamaleclub.ucmc.tradeBoard.domain;


import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Status status = Status.ON_SALE;

    @Column(nullable = false)
    private String locate;

    @Column(nullable = false)
    private String contents;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id", nullable = false)
    private Member member;


    @Builder
    public TradePost (String title, Long price, String locate, String contents,
                         Member member) {
        this.title = title;
        this.price = price;
        this.locate = locate;
        this.contents = contents;
        this.member = member;
    }


    public void  updateTradePost (Status status,String title, Long price, String locate, String contents) {
        this.status = status;
        this.title = title;
        this.price = price;
        this.locate = locate;
        this.contents = contents;
    }




}
