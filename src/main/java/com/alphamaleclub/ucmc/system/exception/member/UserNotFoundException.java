package com.alphamaleclub.ucmc.system.exception.member;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
