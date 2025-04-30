package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.IllegalCookieNameException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookiesManager {

    final int ACCESS_TOKEN_COOKIE_LIVE_TIME = 60 * 60; //1시간
    final int REFRESH_TOKEN_COOKIE_LIVE_TIME = 60 * 60 * 24 * 7; //7일

    public Cookie makeCookie(String tokenValue, String cookieName) {
        return switch (cookieName) {
            case "accessToken" -> makeCookieLogic(cookieName, tokenValue, ACCESS_TOKEN_COOKIE_LIVE_TIME);
            case "refreshToken" -> makeCookieLogic(cookieName, tokenValue, REFRESH_TOKEN_COOKIE_LIVE_TIME);
            default -> throw new IllegalCookieNameException(ExceptionMessage.Auth.ILLEGAL_COOKIE_NAME + cookieName);
        };
    }

    public Cookie makeCookie(String cookieName,String tokenValue,int customTime) {
        return makeCookieLogic(cookieName, tokenValue, customTime);
    }

    private Cookie makeCookieLogic(String cookieName,String tokenValue,int lifeTime){

        boolean isHttpOnly = cookieName.equals("refreshToken");

        Cookie cookie = new Cookie(cookieName,tokenValue);
//        cookie.setMaxAge(lifeTime); //세션쿠키로 지정하기 위해 해당 줄 주석처리
        cookie.setPath("/");
        cookie.setHttpOnly(isHttpOnly);
        cookie.setSecure(false); //개발환경에서는 false(http), 배포시 true(https)
        return cookie;
    }



}
