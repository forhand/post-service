package org.example.handler.exception;

public class PostAlreadyPublishedException extends RuntimeException {
  private static String MESSAGE = "Post has already been published";

  public PostAlreadyPublishedException() {
    super(MESSAGE);
  }
}