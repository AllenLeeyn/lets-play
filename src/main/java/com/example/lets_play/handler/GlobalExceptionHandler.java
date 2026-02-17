package com.example.lets_play.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.example.lets_play.dto.Error;

/**
 * Global exception handler for all REST controllers.
 * Converts thrown exceptions into a consistent {@link Error} response body with
 * appropriate HTTP status codes, as defined in the OpenAPI spec.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles failed authentication (e.g. invalid email or password on signin).
     * Returns 401 Unauthorized.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handleBadCredentials(BadCredentialsException e) {
        return error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    /**
     * Handles conflicting state (e.g. email already registered on signup).
     * Returns 409 Conflict.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Error> handleConflict(IllegalStateException e) {
        return error(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * Handles validation failures from {@code @Valid} request bodies.
     * Builds a message from all field errors and returns 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return error(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles authorization failures (e.g. non-admin accessing admin-only endpoints).
     * Returns 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Error> handleAccessDenied(AccessDeniedException e) {
        return error(HttpStatus.FORBIDDEN, e.getMessage());
    }

    /**
     * Handles {@link ResponseStatusException} (e.g. 404 for missing resource).
     * Preserves the exception's status code and reason in the response body.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Error> handleResponseStatus(ResponseStatusException e) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        return error(status, e.getReason() != null ? e.getReason() : status.getReasonPhrase());
    }

    /** Builds a response with the standard Error body. */
    private static ResponseEntity<Error> error(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new Error(message, status.value()));
    }
}
