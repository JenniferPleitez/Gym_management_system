package com.app.gym.excepciones;

public class ContrasenaIgualExcepcion extends RuntimeException {
    public ContrasenaIgualExcepcion(String mensaje) {
        super(mensaje);
    }
}
