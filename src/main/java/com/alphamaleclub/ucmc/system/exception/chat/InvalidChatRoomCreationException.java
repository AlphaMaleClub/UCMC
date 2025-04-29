package com.alphamaleclub.ucmc.system.exception.chat;

public class InvalidChatRoomCreationException extends RuntimeException {
  public InvalidChatRoomCreationException() {
    super();
  }

  public InvalidChatRoomCreationException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidChatRoomCreationException(Throwable cause) {
    super(cause);
  }

  protected InvalidChatRoomCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public InvalidChatRoomCreationException(String message) {
    super(message);
  }
}
