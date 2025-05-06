package com.alphamaleclub.ucmc.system.exception.auth;

public class InvalidSignUpRequestException extends RuntimeException {
    public InvalidSignUpRequestException(String message) {
        super(message);
    }
}
