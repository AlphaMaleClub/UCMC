package com.alphamaleclub.ucmc.system.exception.auth;

public class MissingTokenException extends RuntimeException {
    public MissingTokenException(String message) {
        super(message);
    }
}
