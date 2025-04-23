package com.alphamaleclub.ucmc.system.exception.auth;

public class MissingTokenException extends Exception {
    public MissingTokenException(String message) {
        super(message);
    }
}
