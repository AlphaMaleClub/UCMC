package com.alphamaleclub.ucmc.member.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpTempMember {

    @Id
    private String id;

    private String provider;
    private String realName;
    private String nickname;
    private String email;
    private String mobile;
    private String loginMethod;
    private String role;
    

}
