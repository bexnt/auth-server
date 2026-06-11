package com.bexnt.authserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                List.of(exception.getMessage())
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage(),
                List.of(exception.getMessage())
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = "Validation failed";
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message,
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
