package com.app.gym.excepciones;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}