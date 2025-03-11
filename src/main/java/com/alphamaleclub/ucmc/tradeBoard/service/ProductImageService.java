package com.alphamaleclub.ucmc.tradeBoard.service;

import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;

public interface ProductImageService {
    ProductImage createTradeProductImage(Long postNum, String imageUrl);
}
