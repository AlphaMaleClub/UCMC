package com.alphamaleclub.ucmc.tradeBoard.service.impl;

import com.alphamaleclub.ucmc.image.domain.PostType;
import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.tradeBoard.repository.ProductImageRepository;
import com.alphamaleclub.ucmc.tradeBoard.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    @Override
    public ProductImage createTradeProductImage(Long postNum, String imageUrl) {

        ProductImage productImage = ProductImage.builder()
                .postType(PostType.AUCTION)
                .postNumber(postNum)
                .imageUrl(imageUrl)
                .build();
        productImageRepository.save(productImage);

        return productImage;
    }


}
