package com.alphamaleclub.ucmc.system.exception.tradeboard;

public class NotAuthorException extends RuntimeException {
    public NotAuthorException() {
    }

    public NotAuthorException(String message) {
        super(message);
    }

    public NotAuthorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthorException(Throwable cause) {
        super(cause);
    }

    public NotAuthorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
