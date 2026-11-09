package com.app.gym.excepciones;

public class UsuarioSinMembresiaExepcion extends RuntimeException {
    public UsuarioSinMembresiaExepcion(String mensaje) {
        super(mensaje);
    }
    
}
