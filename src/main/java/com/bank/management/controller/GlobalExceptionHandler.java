package com.bank.management.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==========================================
    // Validation Errors (existing - unchanged)
    // ==========================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return errors;
    }


    // ==========================================
    // Generic Runtime Errors - Map to proper
    // HTTP status codes based on message content
    // ==========================================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException ex) {

        String message = ex.getMessage() != null
                ? ex.getMessage()
                : "Something went wrong";

        String lowerMessage = message.toLowerCase();

        HttpStatus status;

        if (lowerMessage.contains("not logged in")) {

            status = HttpStatus.UNAUTHORIZED; // 401

        } else if (lowerMessage.contains("invalid email or password") ||
                   lowerMessage.contains("invalid username or password")) {

            status = HttpStatus.UNAUTHORIZED; // 401

        } else if (lowerMessage.contains("not found")) {

            status = HttpStatus.NOT_FOUND; // 404

        } else if (lowerMessage.contains("already exists") ||
                   lowerMessage.contains("insufficient balance") ||
                   lowerMessage.contains("must be greater than") ||
                   lowerMessage.contains("expired") ||
                   lowerMessage.contains("invalid or expired")) {

            status = HttpStatus.BAD_REQUEST; // 400

        } else {

            status = HttpStatus.INTERNAL_SERVER_ERROR; // 500 (fallback)

        }

        Map<String, String> body = new HashMap<>();
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}