package com.alphamaleclub.ucmc.system.exception.auction;

public class AuctionDoesNotExistException extends RuntimeException {
    public AuctionDoesNotExistException(String message) {
        super(message);
    }
}
