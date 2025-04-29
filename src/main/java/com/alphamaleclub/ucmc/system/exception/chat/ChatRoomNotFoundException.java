package com.alphamaleclub.ucmc.system.exception.chat;

public class ChatRoomNotFoundException extends RuntimeException {
  public ChatRoomNotFoundException() {
  }

  public ChatRoomNotFoundException(String message) {
    super(message);
  }

  public ChatRoomNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ChatRoomNotFoundException(Throwable cause) {
    super(cause);
  }

  public ChatRoomNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
