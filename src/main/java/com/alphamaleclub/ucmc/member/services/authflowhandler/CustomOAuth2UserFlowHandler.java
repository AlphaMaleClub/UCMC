package com.alphamaleclub.ucmc.member.services.authflowhandler;

import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomOAuth2UserFlowHandler extends AuthFlowHandler {

    @Override
    public boolean supports(Object principal) {
        return principal instanceof CustomOAuth2User;
    }

    @Override
    protected void doHandle(Object principal) {

        /*
            CustomOAuth2User 가 반환된다면 loadUser 에서 멤버조회에 실패했다는 뜻.
            대신에 인증 자체는 완료이므로 기본적인 정보는 파싱해서 가지고 있는 상태.
            목적에 따라 회원가입을 시키던지 회원가입으로 유도하던지 분기할 것.
         */
        switch (super.intent){
            case "login" -> log.info("login Failure Banned or Member Not Found");
            case "signup" -> log.error("SignUp Logic Methode");
        }

    }
}
