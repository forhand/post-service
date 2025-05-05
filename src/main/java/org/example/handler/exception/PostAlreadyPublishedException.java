package org.example.handler.exception;

public class PostAlreadyPublishedException extends RuntimeException {
  public PostAlreadyPublishedException(String message) {
    super(message);
  }
}