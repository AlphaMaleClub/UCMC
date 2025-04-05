package com.alphamaleclub.ucmc.system.exception.S3Image;

public class MaxFileLimitExceededException extends RuntimeException {
    public MaxFileLimitExceededException(String message) {
        super(message);
    }
}
