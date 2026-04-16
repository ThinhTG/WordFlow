package com.wordflow.backend.exception;

public class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }
}