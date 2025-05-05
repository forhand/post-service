package org.example.handler.exception;

public class AuthorChangeForbiddenException extends RuntimeException {
  public AuthorChangeForbiddenException(String message) {
    super(message);
  }
}