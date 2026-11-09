package com.app.gym.excepciones;

public class ContrasenaInvalidaExcepcion extends RuntimeException {
    public ContrasenaInvalidaExcepcion(String mensaje) {
        super(mensaje);
    }
}
