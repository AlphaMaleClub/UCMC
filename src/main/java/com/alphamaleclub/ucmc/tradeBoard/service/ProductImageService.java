package com.alphamaleclub.ucmc.tradeBoard.service;

import com.alphamaleclub.ucmc.image.domain.PostType;
import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;

import java.util.List;

public interface ProductImageService {
    ProductImage createTradeProductImage(Long postNum, String imageUrl);

    List<ProductImage> getTradeProductImagesByPostTypeAndPostNumber(PostType postType, Long postNumber);

    void deleteProductImage(ProductImage productImage);
}
