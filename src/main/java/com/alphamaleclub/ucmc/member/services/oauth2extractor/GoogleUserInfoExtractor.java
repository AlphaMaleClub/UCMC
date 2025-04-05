package com.alphamaleclub.ucmc.member.services.oauth2extractor;

import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoogleUserInfoExtractor implements Oauth2UserInfoExtractor {

    @Override
    public boolean supports(String provider) {
        return provider.equals("google");
    }

    @Override
    public CustomOAuth2User extract(OAuth2User oAuth2User, String provider) {

        Map<String, Object> attributes = oAuth2User.getAttributes();

        return CustomOAuth2User.builder()
                .attributes(attributes)
                .provider(provider)
                .realName((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .role("Unregistered")
                .build();
    }

}
