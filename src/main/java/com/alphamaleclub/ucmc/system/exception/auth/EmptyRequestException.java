package com.alphamaleclub.ucmc.system.exception.auth;

public class EmptyRequestException extends RuntimeException {
    public EmptyRequestException(String message) {
        super(message);
    }
}
