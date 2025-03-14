package com.alphamaleclub.ucmc.tradeBoard.dto;

import com.alphamaleclub.ucmc.image.domain.PostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadTradePostRequest {

    PostType postType;

}
