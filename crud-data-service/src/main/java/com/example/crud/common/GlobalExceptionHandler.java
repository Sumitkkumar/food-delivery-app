package com.example.crud.common;

import org.bson.json.JsonParseException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        var msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .findFirst().orElse("Validation error");
        return ResponseEntity.badRequest().body(ApiError.of(msg, 400));
    }

    // Invalid ObjectId OR wrong format
    @ExceptionHandler({IllegalArgumentException.class, JsonParseException.class, ConversionFailedException.class})
    public ResponseEntity<?> handleBadId(Exception ex) {
        return ResponseEntity.badRequest().body(ApiError.of(ex.getMessage(), 400));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(404).body(ApiError.of(ex.getMessage(), 404));
    }

    // Mongo or DB failure
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleMongo(DataAccessException ex) {
        return ResponseEntity.status(503)
                .body(ApiError.of("Database error: " + ex.getMessage(), 503));
    }

    // fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAny(Exception ex) {
        return ResponseEntity.status(500).body(ApiError.of(ex.getMessage(), 500));
    }
}