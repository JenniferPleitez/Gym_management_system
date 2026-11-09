package com.app.gym.excepciones;

public class EmailYaExisteExcepcion extends RuntimeException {
    public EmailYaExisteExcepcion(String mensaje) {
        super(mensaje);
    }
}
