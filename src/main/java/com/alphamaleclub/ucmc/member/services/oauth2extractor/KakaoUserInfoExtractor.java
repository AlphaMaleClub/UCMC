package com.alphamaleclub.ucmc.member.services.oauth2extractor;

import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidOAuth2AttributesException;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidOAuth2ProviderException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@SuppressWarnings("unchecked")
public class KakaoUserInfoExtractor implements Oauth2UserInfoExtractor {

    @Override
    public boolean supports(String provider) {
        return provider.equals("kakao");
    }

    @Override
    public CustomOAuth2User extract(OAuth2User oAuth2User, String provider) {
        /*
            카카오는 닉네임만 받아오는게 아니라 아이디 값이 있다.
            카카오의 정보는 중첩 키밸류로 되어있기 때문에
            파싱과정 전체에 생길수 있는 예외를 멀티캐칭해서 던지게끔 설계했다.
         */
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String nickname;

        try {
            nickname = (String) ((Map<String, Object>) ((Map<String, Object>) attributes.get("kakao_account")).get("profile")).get("nickname");
        }catch (NullPointerException | ClassCastException e){
            throw new InvalidOAuth2ProviderException(ExceptionMessage.Auth.INVALID_OAUTH2_PROVIDER + "provider = " + provider);
        }


        return CustomOAuth2User.builder()
                .attributes(attributes)
                .provider(provider)
                .email(attributes.get("id") + "@kakao.com")
                .nickname(nickname)
                .role("Unregistered")
                .build();
    }

}
