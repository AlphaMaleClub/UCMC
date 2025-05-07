package com.alphamaleclub.ucmc.member.services.authflowhandler;

import com.alphamaleclub.ucmc.member.domain.SignUpTempMember;
import com.alphamaleclub.ucmc.member.dto.CustomOAuth2User;
import com.alphamaleclub.ucmc.member.services.MemberService;
import com.alphamaleclub.ucmc.system.exception.member.UserNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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

                SignUpTempMember tempMember = checkTempUser(user);

                if(tempMember != null){
                    return makeURL(tempMember);
                }

                log.info("login Failure Banned or Member Not Found");
                return LOGIN_FAILED_URL;
            }
            case "signup" -> {
//                memberService.signUp(SignUpRequest.fromCustomOAuth2UserTestOnly(user));
                return signupProcess(user);
            }

        }
        return null;
    }

    private SignUpTempMember checkTempUser(CustomOAuth2User user) {

        try{
            return memberService.getTempUserByEmail(user.getEmail());
        }catch(UserNotFoundException e){
            log.info("this Member intent does not invalid");
        }
        return null;

    }

    private String signupProcess(CustomOAuth2User user) {

        SignUpTempMember tempMember;

        tempMember = checkTempUser(user);

        if(tempMember == null){
            tempMember = memberService.tempUserSave(user);
        }

        return makeURL(tempMember);

    }

    private String makeURL(SignUpTempMember tempMember) {
        return UriComponentsBuilder
                .fromUriString(SIGNUP_SUCCESS_URL)
                .queryParam("provider", tempMember.getProvider())
                .queryParam("realName", URLEncoder.encode(Optional.ofNullable(tempMember.getRealName()).orElse(""), StandardCharsets.UTF_8))
                .queryParam("nickname", URLEncoder.encode(Optional.ofNullable(tempMember.getNickname()).orElse(""), StandardCharsets.UTF_8))
                .queryParam("email", Optional.ofNullable(tempMember.getEmail()).orElse(""))
                .queryParam("memberNum",tempMember.getId())
                .build()
                .toUriString();
    }

}
