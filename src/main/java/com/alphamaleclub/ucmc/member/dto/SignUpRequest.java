package com.alphamaleclub.ucmc.member.dto;

import lombok.Data;

@Data
public class SignUpRequest {

    private String accountId;
    private String password;
    private String nickname;
    private String email;
    private String mobile;
    private String provider;

}
