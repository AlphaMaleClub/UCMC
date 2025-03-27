package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
public class CustomOAuth2Handler implements AuthenticationSuccessHandler {

    String intent = null;
    String provider = null;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        setCookieValue(request);

        Object principal = authentication.getPrincipal();

        if(principal instanceof CustomUserDetails) {
            if(intent.equals("login")) {
                log.info("Login successful");
            }else if(intent.equals("signup")) {
                log.error("already Signed in");
            }


        }else if(principal instanceof OAuth2User) {
            if(intent.equals("login")) {
                log.error("login Failure Banned or Member Not Found");
            }else if(intent.equals("signup")) {
                log.info("SignUp Logic Methode");
            }
        }

    }

    public void setCookieValue(HttpServletRequest request) {
        if(request.getCookies() != null){
            for(Cookie cookie: request.getCookies()){
                if(cookie.getName().equals("intent")){
                    intent = cookie.getValue();
                } else if(cookie.getName().equals("provider")){
                    provider = cookie.getValue();
                }
            }
        }
    }

    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    }



}
