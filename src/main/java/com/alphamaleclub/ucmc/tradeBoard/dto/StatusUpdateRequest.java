package com.alphamaleclub.ucmc.tradeBoard.dto;

import com.alphamaleclub.ucmc.tradeBoard.domain.Status;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    private Status status;
}
