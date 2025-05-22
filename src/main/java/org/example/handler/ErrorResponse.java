package org.example.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String url;
    private int status;
    private String message;
    private List<String> errors;

    public ErrorResponse(String message) {
        this.message = message;
    }


}
