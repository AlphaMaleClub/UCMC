package com.alphamaleclub.ucmc.member.dto;

import com.alphamaleclub.ucmc.member.domain.Role;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@Data
public class CustomOAuth2User implements OAuth2User {

    /*
        이 Dto 클래스는 loadUser 메서드 -> SuccessHandler 과정에서
        멤버 조회 실패시 Oauth2.0 인증 정보를 기반으로
        oAuth2User -> SignUpRequestDto 를 사전에 채워둔 상태로
        넘겨주기 위해서 만들었습니다.
     */

    private String provider;
    private String realName;
    private String nickname;
    private String email;
    private String mobile;

    @Setter
    private String role;

    private Map<String, Object> attributes;

    @Builder
    public CustomOAuth2User(String provider, String realName, String nickname, String email, String mobile,String role, Map<String, Object> attributes) {
        this.provider = provider;
        this.realName = realName;
        this.nickname = nickname;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getName() {
        return this.email;
    }

}
