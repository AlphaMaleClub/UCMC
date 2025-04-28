package com.alphamaleclub.ucmc.system.exception.auth;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
