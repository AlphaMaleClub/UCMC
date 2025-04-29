package com.alphamaleclub.ucmc.tradeBoard.service.impl;

import com.alphamaleclub.ucmc.image.domain.PostType;
import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.tradeBoard.dto.GetTradePostImageResponse;
import com.alphamaleclub.ucmc.tradeBoard.repository.ProductImageRepository;
import com.alphamaleclub.ucmc.tradeBoard.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<ProductImage> getTradeProductImagesByPostNumber(Long postNumber) {

        List<ProductImage> images = productImageRepository.findByPostNumber(postNumber);

        return images;

    }

    @Override
    public void deleteProductImage(ProductImage productImage) {
        productImageRepository.delete(productImage);
    }

    @Override
    public Page<ProductImage> findAllProductImagesOnlyTradePost(Pageable pageable) {

        Page<ProductImage> images = productImageRepository.findByPostType(PostType.TRADE, pageable);

        return images;
    }


    @Override
    public ProductImage getProductImageByPostNumber(Long postNumber) {

        List<ProductImage> byPostNumber = productImageRepository.findByPostNumber(postNumber);

        if (byPostNumber.isEmpty()) {
            return null;
        }

        ProductImage result = byPostNumber.get(0);

        return result;
    }


    @Override
    public GetTradePostImageResponse getProductImageFirstByPostNumber(Long postNumber) {

        List<ProductImage> byPostNumber = productImageRepository.findByPostNumber(postNumber);

        ProductImage productImage = byPostNumber.get(0);

        GetTradePostImageResponse result = GetTradePostImageResponse.builder()
                .message("Success Get Product Image By Post Number")
                .result(true)
                .productImage(productImage)
                .build();

        return result;
    }


}
