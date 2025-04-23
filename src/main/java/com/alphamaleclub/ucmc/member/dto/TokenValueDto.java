package com.alphamaleclub.ucmc.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Data
@Slf4j
@Builder
public class TokenValueDto {

    private String id;
    private String nickname;
    private String role;
    private boolean isRefreshToken;

}
