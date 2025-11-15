package com.jokardo.crm.order_service.exceptions;

import com.jokardo.crm.order_service.exceptions.image.ImageUploadException;
import com.jokardo.crm.order_service.exceptions.order.OrderCannotBeUpdatedException;
import com.jokardo.crm.order_service.exceptions.order.OrderNotFoundException;
import com.jokardo.crm.order_service.exceptions.product.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ExceptionResponse>  handleProductNotFoundException(ProductNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.NOT_FOUND,
                "Product not found.",
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @ExceptionHandler(ImageUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleImageUploadException(ImageUploadException ex) {
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                "Internal error.", ex.getMessage(), LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(OrderCannotBeUpdatedException.class)
    public ResponseEntity<ExceptionResponse> handleOrderCannotBeUpdatedException(OrderCannotBeUpdatedException ex) {
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                "Order can not be updated!",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.NOT_FOUND,
                "Order can not be updated, because he's not found!",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        StringBuilder sb = new StringBuilder();
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();

        for (int i = 0; i < errors.size() - 1; i++)
            sb.append(errors.get(i).getDefaultMessage() + " ");

        sb.append(errors.get(errors.size() - 1).getDefaultMessage());

        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                "Validation exception.",
                sb.toString(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                "Illegal argument exception",
                ex.getLocalizedMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
