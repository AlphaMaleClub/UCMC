package com.alphamaleclub.ucmc.member.services.authflowhandler;

import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidAccessPathException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AuthFlowHandler {

    protected String intent;
    protected String provider;

    abstract public boolean supports(Object principal);

    public final String handle(Cookie[] cookies, Object principal, HttpServletResponse response) {

        /*
           doHandle 은 각각 분기마다 맞는 수행을 합니다.
           그리고 어디로 Redirect 시킬지 경로를 반환시킵니다.
           폼로그인일 경우 쿠키를 사용하지 않기 때문에 intent와 provider를 수동으로 설정합니다.
        */
        if(checkLoginMethodIsOAuth(principal)) {
            setCookieValue(cookies);
        }else{
            this.intent = "login";
            this.provider = "none";
        }

        return doHandle(response, principal);
    };

    protected abstract String doHandle(HttpServletResponse response, Object principal);

    private void setCookieValue(Cookie[] cookies) throws InvalidAccessPathException {

        if(cookies != null){
            for(Cookie cookie: cookies){
                if(cookie.getName().equals("intent")){
                    this.intent = cookie.getValue();
                } else if(cookie.getName().equals("provider")){
                    provider = cookie.getValue();
                }
            }
        }

        if( intent == null || provider == null ){
            throw new InvalidAccessPathException(ExceptionMessage.Auth.INVALID_ACCESS_PATH_EXCEPTION);
        }

    }

    private boolean checkLoginMethodIsOAuth(Object principal) {
        CustomUserDetails user = (CustomUserDetails) principal;
        return user.getLoginMethod().equals("oauth2");
    }

}
