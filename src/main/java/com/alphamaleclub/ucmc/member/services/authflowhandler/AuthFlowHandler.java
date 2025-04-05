package com.alphamaleclub.ucmc.member.services.authflowhandler;

import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidAccessPathException;
import jakarta.servlet.http.Cookie;

public abstract class AuthFlowHandler {

    protected String intent;
    protected String provider;

    abstract public boolean supports(Object principal);

    public final void handle(Cookie[] cookies, Object principal){
        setCookieValue(cookies);
        doHandle(principal);
    };

    protected abstract void doHandle(Object principal);

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
