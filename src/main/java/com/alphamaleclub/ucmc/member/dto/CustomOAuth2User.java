package com.alphamaleclub.ucmc.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@Getter
@Builder
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
    private String loginMethod;

    @Setter
    private String role;

    private Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    @Deprecated //우리는 이거 직접적으로 쓰지 않음 Security 내부에서 쓰니까 오버라이드 한 것.
    public String getName() {
        return this.email;
    }

}
