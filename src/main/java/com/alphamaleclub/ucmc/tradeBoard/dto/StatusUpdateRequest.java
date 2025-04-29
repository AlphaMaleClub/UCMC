package com.alphamaleclub.ucmc.tradeBoard.dto;

import com.alphamaleclub.ucmc.tradeBoard.domain.TradeStatus;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    private TradeStatus tradeStatus;
}
