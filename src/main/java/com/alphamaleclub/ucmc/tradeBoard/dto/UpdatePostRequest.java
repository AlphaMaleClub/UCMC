package com.alphamaleclub.ucmc.tradeBoard.dto;


import com.alphamaleclub.ucmc.image.domain.PostType;
import com.alphamaleclub.ucmc.tradeBoard.domain.DeliveryType;
import com.alphamaleclub.ucmc.tradeBoard.domain.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest {

    PostType postType;

    Long postNumber;

    Status status;

    String title;

    String content;

    Long price;

    String Locate;

    DeliveryType deliveryType;

    Long dumpedCount;

    LocalDateTime updatedAt;
}
