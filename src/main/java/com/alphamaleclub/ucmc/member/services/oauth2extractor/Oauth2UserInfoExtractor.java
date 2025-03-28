package com.alphamaleclub.ucmc.member.services.oauth2extractor;

import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface Oauth2UserInfoExtractor {

    boolean supports(String provider);
    CustomOAuth2User extract(OAuth2User oAuth2User, String provider);

}
