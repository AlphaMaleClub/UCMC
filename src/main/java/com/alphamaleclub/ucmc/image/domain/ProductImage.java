package com.alphamaleclub.ucmc.image.domain;


import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id", nullable = false)
    private long id;

    @Column(nullable = false, unique = true)
    private String imageUrl; // S3 등록 되는 URL 경로? -> 이게 실질적인 이름//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_post_id", nullable = false)
    private TradePost tradePost;

    @Builder
    public ProductImage(String imageUrl, TradePost tradePost) {
        this.imageUrl = imageUrl;
        this.tradePost = tradePost;
    }

}
