package com.alphamaleclub.ucmc.member.services.authflowhandler;

import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.member.dto.TokenPair;
import com.alphamaleclub.ucmc.member.services.CookiesManager;
import com.alphamaleclub.ucmc.member.services.MemberService;
import com.alphamaleclub.ucmc.member.services.TokenManager;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.AccountAlreadyExistsException;
import com.alphamaleclub.ucmc.system.util.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserDetailsFlowHandler extends AuthFlowHandler {

    private final TokenManager tokenManager;
    private final CookiesManager cookiesManager;
    private final MemberService memberService;

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
        CustomUserDetails userDetails = (CustomUserDetails) principal;


        switch (super.intent) {

            case "login" -> {
                return loginSuccess(response, userDetails);
            }
            case "signup" -> {
                log.warn("Already Exist AccountId = {}",  userDetails.getUserId());
                return SIGNUP_FAILED_URL;
            }

        }
        return null;
    }


    private String loginSuccess(HttpServletResponse response,CustomUserDetails userDetails) {



        Member member = memberService.getMemberById(SecurityUtil.getCurrentMemberId());

        tokenManager.expireRefreshToken(member);

        TokenPair tokenPair = tokenManager.generateTokenPair(userDetails);

        tokenManager.saveRefreshToken(tokenPair.getAccessToken());

        String accessToken = tokenPair.getAccessToken();
        String refreshToken = tokenPair.getRefreshToken();

        //refreshToken 은 쿠키로
        response.addCookie(cookiesManager.makeCookie(refreshToken,"refreshToken"));

        //accessToken 은 읽을 수 있는 쿠키로
        response.addCookie(cookiesManager.makeCookie(accessToken,"accessToken"));


//         accessToken 은 커스텀 응답으로 줌(login Controller 없이 handler 에서 처리하기 때문에)
//        try{
//            response.setContentType("application/json");
//            response.getWriter().write("{\"accessToken\": \"" + accessToken + "\"}");
//        }catch(IOException e){
//            log.error("응답을 쓸 수 없습니다. 네트워크 에러입니다. {}", e.getMessage());
//        }


        log.info("Login User : [UserId = {}] [UserNickName = {}]", userDetails.getUserId(),userDetails.getNickname());
        return LOGIN_SUCCESS_URL;

    }

}
