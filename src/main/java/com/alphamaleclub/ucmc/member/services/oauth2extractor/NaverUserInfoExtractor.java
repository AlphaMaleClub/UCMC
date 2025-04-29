package com.alphamaleclub.ucmc.member.services.oauth2extractor;

import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidOAuth2AttributesException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NaverUserInfoExtractor implements Oauth2UserInfoExtractor {

    @Override
    public boolean supports(String provider) {
        return provider.equals("naver");
    }

    @Override
    public CustomOAuth2User extract(OAuth2User oAuth2User, String provider) {

        /*
            네이버는 실질적인 반환값들이 response 라는 키 안에 Json 으로 존재하므로 파싱해야함
            추 후에 구조가 바뀐다면 여기서 예외가 터짐.
        */

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Object responseObj = attributes.get("response");

        if(!(responseObj instanceof Map)){
            throw new InvalidOAuth2AttributesException(ExceptionMessage.Auth.OAUTH2_CANNOT_FOUND_ATTRIBUTES + "provider = " + provider);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) responseObj;

        return CustomOAuth2User.builder()
                .attributes(attributes)
                .provider(provider)
                .email((String) response.get("email"))
                .nickname((String) response.get("nickname"))
                .mobile((String) response.get("mobile"))
                .realName((String) response.get("name"))
                .role("Unregistered")
                .build();

    }

}
