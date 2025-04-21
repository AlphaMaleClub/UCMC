package com.alphamaleclub.ucmc.member.services.authflowhandler;

import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomUserDetailsFlowHandler extends AuthFlowHandler {

    @Override
    public boolean supports(Object principal) {
        return principal instanceof CustomUserDetails;
    }

    @Override
    protected void doHandle(Object principal) {

        /*
            CustomUserDetails 가 반환된다면 loadUser 에서 조회 성공했다는 뜻.
            따라서 기존회원의 정보가 모두 들어있으므로 이후 토큰을 발급하던지
            상태를 보고 판단할 것.
            signup 으로 들어온 요청은 이미 기존 회원이므로 login 으로 유도할지에 대해
            프론트에서 처리할 것.
         */
        switch (super.intent){
            case "login" -> log.info("Login successful");
            case "signup" -> log.error("already Signed in");
        }

    }
}
