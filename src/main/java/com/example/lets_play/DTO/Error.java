package com.example.lets_play.dto;

/**
 * Standard error response body for 4xx responses. Used by {@link com.example.lets_play.handler.GlobalExceptionHandler}.
 */
public class Error {

    private String message;
    private int status;

    public Error() {}

    public Error(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
