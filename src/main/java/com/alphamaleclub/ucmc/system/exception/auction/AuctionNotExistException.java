package com.alphamaleclub.ucmc.system.exception.auction;

public class AuctionNotExistException extends RuntimeException {
    public AuctionNotExistException(String message) {
        super(message);
    }
}
