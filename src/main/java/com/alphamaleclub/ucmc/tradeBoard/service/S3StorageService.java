package com.alphamaleclub.ucmc.tradeBoard.service;

import java.io.IOException;

public interface S3StorageService {
    String upload(byte[] file, String fileName) throws IOException;
}
