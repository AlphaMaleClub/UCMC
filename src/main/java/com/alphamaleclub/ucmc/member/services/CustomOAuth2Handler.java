package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.member.services.authflowhandler.AuthFlowHandler;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidPrincipalTypeException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2Handler extends SimpleUrlAuthenticationSuccessHandler {

    private final List<AuthFlowHandler> authFlowHandlers;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();
        Cookie[] cookies = request.getCookies();

        authFlowHandlers.stream()
                .filter(handler -> handler.supports(principal))
                .findFirst()
                .orElseThrow(() -> new InvalidPrincipalTypeException(ExceptionMessage.Auth.INVALID_PRINCIPAL_TYPE + ": " + principal))
                .handle(cookies, principal);
    }

    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
    }



}
