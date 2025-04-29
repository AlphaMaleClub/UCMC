package com.alphamaleclub.ucmc.system.exception.member;

public class LoginRequiredException extends RuntimeException {
    public LoginRequiredException(String message) {
        super(message);
    }
}
