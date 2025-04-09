package com.alphamaleclub.ucmc.member.services.authflowhandler;

import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidAccessPathException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public abstract class AuthFlowHandler {

    protected String intent;
    protected String provider;

    abstract public boolean supports(Object principal);

    public final String handle(Cookie[] cookies, Object principal, HttpServletResponse response) {
        /*
           doHandle 은 각각 분기마다 맞는 수행을 합니다.
           그리고 어디로 Redirect 시킬지 경로를 반환시킵니다.
        */

        setCookieValue(cookies);
        return doHandle(response, principal);
    };

    protected abstract String doHandle(HttpServletResponse response, Object principal);

    public void setCookieValue(Cookie[] cookies) throws InvalidAccessPathException {

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


}
