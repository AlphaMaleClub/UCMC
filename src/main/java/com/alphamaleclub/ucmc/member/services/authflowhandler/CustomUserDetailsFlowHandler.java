package com.alphamaleclub.ucmc.member.services.authflowhandler;

import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.member.dto.TokenPair;
import com.alphamaleclub.ucmc.member.services.CookiesManager;
import com.alphamaleclub.ucmc.member.services.TokenManager;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.AccountAlreadyExistsException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserDetailsFlowHandler extends AuthFlowHandler {

    private final TokenManager tokenManager;
    private final CookiesManager cookiesManager;

    @Value("${success-handler.redirect-url.login-success}")
    private String LOGIN_SUCCESS_URL;

    @Value("${success-handler.redirect-url.signup-failed}")
    private String SIGNUP_FAILED_URL;


    @Override
    public boolean supports(Object principal) {
        return principal instanceof CustomUserDetails;
    }

    @Override
    protected String doHandle(HttpServletResponse response, Object principal) {

        /*
            CustomUserDetails 가 반환된다면 loadUser 에서 조회 성공했다는 뜻.
            따라서 기존회원의 정보가 모두 들어있으므로 이후 토큰을 발급하던지
            상태를 보고 판단할 것.
            signup 으로 들어온 요청은 이미 기존 회원이므로 login 으로 유도할지에 대해
            프론트에서 처리할 것.
         */

        switch (super.intent) {
            case "login" -> {
                return loginSuccess(response, principal);
            }
            case "signup" -> {
                return SIGNUP_FAILED_URL;
            }
        }
        return null;
    }


    private String loginSuccess(HttpServletResponse response,Object principal) {

        CustomUserDetails userDetails = (CustomUserDetails) principal;

        TokenPair tokenPair = tokenManager.generateTokenPair(userDetails);

        tokenManager.saveRefreshToken(tokenPair.getAccessToken());

        String accessToken = tokenPair.getAccessToken();
        String refreshToken = tokenPair.getRefreshToken();

        response.addCookie(cookiesManager.makeCookie(accessToken,"accessToken"));
        response.addCookie(cookiesManager.makeCookie(refreshToken,"refreshToken"));

        return LOGIN_SUCCESS_URL;

    }

}
