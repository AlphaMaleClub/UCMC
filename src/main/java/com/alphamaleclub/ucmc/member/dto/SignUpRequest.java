package com.alphamaleclub.ucmc.member.dto;

import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SignUpRequest {

    private String accountId;
    private String password;
    private String nickname;
    private String email;
    private String mobile;
    private String provider;

    public static SignUpRequest fromCustomOAuth2UserTestOnly(CustomOAuth2User user) {
        return SignUpRequest.builder()
                .accountId(UUID.randomUUID().toString().replace("-", "").substring(0, 12))
                .password(UUID.randomUUID().toString())
                .nickname("TestName")
                .email(user.getEmail())
                .mobile(user.getMobile())
                .provider(user.getProvider())
                .build();
    }

}
