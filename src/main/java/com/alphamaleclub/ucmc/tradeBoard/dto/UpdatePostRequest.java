package com.alphamaleclub.ucmc.tradeBoard.dto;


import com.alphamaleclub.ucmc.image.domain.PostType;
import com.alphamaleclub.ucmc.tradeBoard.domain.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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

}
