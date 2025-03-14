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

    private PostType postType;

    private Long postNumber;

    @Builder
    public ProductImage(PostType postType,Long postNumber, String imageUrl) {
        this.postType = postType;
        this.postNumber = postNumber;
        this.imageUrl = imageUrl;
    }

}
