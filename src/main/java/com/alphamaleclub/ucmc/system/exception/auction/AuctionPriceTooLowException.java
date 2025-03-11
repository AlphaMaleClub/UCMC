package com.alphamaleclub.ucmc.system.exception.auction;

public class AuctionPriceTooLowException extends RuntimeException {
    public AuctionPriceTooLowException(String message) {
        super(message);
    }
}