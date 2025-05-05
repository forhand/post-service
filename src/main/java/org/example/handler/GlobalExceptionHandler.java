package org.example.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.handler.exception.AuthorChangeForbiddenException;
import org.example.handler.exception.PostAlreadyPublishedException;
import org.example.handler.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMethodArgumentNotValidException(
          MethodArgumentNotValidException exception,
          HttpServletRequest request) {
    log.error("Error while validating post creation: {}", exception.getMessage(), exception);
    return buildErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), exception.getMessage());
  }

  @ExceptionHandler(PostAlreadyPublishedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handlePostAlreadyPublishedException(
          PostAlreadyPublishedException exception,
          HttpServletRequest request) {
    log.error("Error while republishing post: {}", exception.getMessage(), exception);
    return buildErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), exception.getMessage());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleResourceNotFoundException(
          ResourceNotFoundException exception,
          HttpServletRequest request) {
    log.error("Resource is not found: {}", exception.getMessage(), exception);
    return buildErrorResponse(request.getRequestURI(), HttpStatus.NOT_FOUND.value(), exception.getMessage());
  }

  @ExceptionHandler(AuthorChangeForbiddenException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleAuthorChangeForbiddenException(
          AuthorChangeForbiddenException exception,
          HttpServletRequest request) {
    log.error("Error while updating post: {}", exception.getMessage(), exception);
    return buildErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), exception.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleRuntimeException(
          RuntimeException exception,
          HttpServletRequest request) {
    log.error("Exception: {}", exception.getMessage(), exception);
    return buildErrorResponse(request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
  }

  private ErrorResponse buildErrorResponse(String requestURI, int httpStatus, String message) {
    return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .url(requestURI)
            .status(httpStatus)
            .message(message)
            .build();
  }
}
