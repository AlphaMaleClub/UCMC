package com.alphamaleclub.ucmc.tradeBoard.repository;

import com.alphamaleclub.ucmc.image.domain.PostType;
import com.alphamaleclub.ucmc.image.domain.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByPostTypeAndPostNumber(PostType postType, Long postNumber);

    Page<ProductImage> findByPostType(PostType postType, Pageable pageable);

}
