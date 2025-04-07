package com.alphamaleclub.ucmc.system.exception.auction;

public class BiddingTooLowException extends RuntimeException {
    public BiddingTooLowException(String message) {
        super(message);
    }
}