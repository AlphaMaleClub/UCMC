package com.alphamaleclub.ucmc.system.exception.S3Image;

public class ImageConvertException extends RuntimeException {
  public ImageConvertException(String message) {
    super(message);
  }

  public ImageConvertException(String message, Throwable cause) {
    super(message, cause); // cause(원래 발생한 예외)를 포함하여 예외를 던짐
  }
}
