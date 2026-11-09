package com.app.gym.excepciones;

public class AppSecurityException extends RuntimeException {
    public AppSecurityException(String message) {
        super(message);
    }

    public AppSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
