package com.app.gym.excepciones;

public class EmailYaVerificadoExcepcion extends RuntimeException {
    public EmailYaVerificadoExcepcion(String message) {
        super(message);
    }
}
