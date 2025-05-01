package org.example.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

        return buildErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), exception.getMessage());
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
