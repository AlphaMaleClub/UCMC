package com.alphamaleclub.ucmc.system.exception.auction;

public class AuctionAlreadyFinishedException extends RuntimeException {
    public AuctionAlreadyFinishedException(String message) {
        super(message);
    }
}
