package com.example.frontend.common;

import com.example.frontend.exception.DownstreamException;
import com.example.frontend.exception.BadRequestException;
import com.example.frontend.exception.ForbiddenException;
import com.example.frontend.exception.NotFoundException;
import com.example.frontend.exception.UnauthorizedException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 400 — validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        var msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        return ResponseEntity.badRequest().body(ApiError.of(msg, 400));
    }

    // 400 — data conversion errors (invalid number, invalid enum, etc.)
    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<?> handleConversion(ConversionFailedException ex) {
        return ResponseEntity.badRequest().body(ApiError.of(ex.getMessage(), 400));
    }

    // 400 — client sent an incorrect request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(ApiError.of(ex.getMessage(), 400));
    }

    // 401 — missing/wrong auth
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(401).body(ApiError.of(ex.getMessage(), 401));
    }

    // 403 — forbidden (ownership rule fail)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(403).body(ApiError.of(ex.getMessage(), 403));
    }

    // 404 — resource not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(404).body(ApiError.of(ex.getMessage(), 404));
    }

    // Error coming from CRUD backend
    @ExceptionHandler(DownstreamException.class)
    public ResponseEntity<?> handleDownstream(DownstreamException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(ex.getParsedBodyOrMessage());
    }

    // Catch-all unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAny(Exception ex) {
        return ResponseEntity.status(500).body(ApiError.of(ex.getMessage(), 500));
    }
}
