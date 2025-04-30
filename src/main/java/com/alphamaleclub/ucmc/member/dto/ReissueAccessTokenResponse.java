package com.alphamaleclub.ucmc.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReissueAccessTokenResponse {

    private String accessToken;
    private String tokenType = "Bearer";

}
