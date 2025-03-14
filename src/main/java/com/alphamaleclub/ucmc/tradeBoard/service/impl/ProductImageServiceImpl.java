package com.alphamaleclub.ucmc.tradeBoard.service.impl;

import com.alphamaleclub.ucmc.image.domain.PostType;
import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.tradeBoard.repository.ProductImageRepository;
import com.alphamaleclub.ucmc.tradeBoard.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    @Override
    public ProductImage createTradeProductImage(Long postNum, String imageUrl) {

        ProductImage productImage = ProductImage.builder()
                .postType(PostType.TRADE)
                .postNumber(postNum)
                .imageUrl(imageUrl)
                .build();
        productImageRepository.save(productImage);

        return productImage;
    }

    @Override
    public List<ProductImage> getTradeProductImagesByPostTypeAndPostNumber(PostType postType, Long postNumber) {

        List<ProductImage> images = productImageRepository.findByPostTypeAndPostNumber(postType, postNumber);

        return images;

    }

    @Override
    public void deleteProductImage(ProductImage productImage) {
        productImageRepository.delete(productImage);
    }

}
