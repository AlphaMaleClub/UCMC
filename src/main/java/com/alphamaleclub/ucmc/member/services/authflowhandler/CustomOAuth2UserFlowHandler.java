package com.alphamaleclub.ucmc.member.services.authflowhandler;

import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import com.alphamaleclub.ucmc.member.dto.SignUpRequest;
import com.alphamaleclub.ucmc.member.services.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserFlowHandler extends AuthFlowHandler {

    private final MemberService memberService;


    @Value("${success-handler.redirect-url.login-failed}")
    private String LOGIN_FAILED_URL;

    @Value("${success-handler.redirect-url.signup-success}")
    private String SIGNUP_SUCCESS_URL;

    @Override
    public boolean supports(Object principal) {
        return principal instanceof CustomOAuth2User;
    }

    @Override
    protected String doHandle(HttpServletResponse response, Object principal) {

        /*
            CustomOAuth2User 가 반환된다면 loadUser 에서 멤버조회에 실패했다는 뜻.
            대신에 인증 자체는 완료이므로 기본적인 정보는 파싱해서 가지고 있는 상태.
            목적에 따라 회원가입을 시키던지 회원가입으로 유도하던지 분기할 것.
         */
        CustomOAuth2User user = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        switch (super.intent){

            case "login" -> {
                log.info("login Failure Banned or Member Not Found");
                return LOGIN_FAILED_URL;
            }
            case "signup" -> {
                memberService.signUp(SignUpRequest.fromCustomOAuth2UserTestOnly(user));
                return SIGNUP_SUCCESS_URL;
            }

        }
        return null;
    }
}
