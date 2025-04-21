package com.alphamaleclub.ucmc.tradeBoard.service;

import com.alphamaleclub.ucmc.image.domain.PostType;
import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.tradeBoard.dto.GetTradePostImageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductImageService {
    ProductImage createTradeProductImage(Long postNum, String imageUrl);

    List<ProductImage> getTradeProductImagesByPostNumber(Long postNumber);

    void deleteProductImage(ProductImage productImage);

    Page<ProductImage> findAllProductImagesOnlyTradePost(Pageable pageable);

    ProductImage getProductImageByPostNumber(Long postNumber);

    GetTradePostImageResponse getProductImageFirstByPostNumber(Long postNumber);
}
