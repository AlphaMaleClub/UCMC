package com.alphamaleclub.ucmc.tradeBoard.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductImageConvertService {

    int VALID_MIN_WIDTH = 500;
    int VALID_MIN_HEIGHT = 500;

    int VALID_MAX_WIDTH = 5000;
    int VALID_MAX_HEIGHT = 5000;

    int MAX_SIZE = 10 * 1024 * 1024; //10MB

    List<byte[]> convert(List<MultipartFile> file) throws IOException;


}
