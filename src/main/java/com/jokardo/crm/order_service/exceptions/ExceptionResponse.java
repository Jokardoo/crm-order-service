package com.jokardo.crm.order_service.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ExceptionResponse {
    private String message;
    private String detailMessage;
    private LocalDateTime timestamp;
    private HttpStatus status;

    public ExceptionResponse(HttpStatus status, String message, String detailMessage, LocalDateTime timestamp) {
        this.message = message;
        this.detailMessage = detailMessage;
        this.timestamp = timestamp;
        this.status = status;
    }
}
