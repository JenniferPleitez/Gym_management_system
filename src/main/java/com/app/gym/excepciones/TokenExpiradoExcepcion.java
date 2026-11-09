package com.app.gym.excepciones;

public class TokenExpiradoExcepcion extends RuntimeException{
    public TokenExpiradoExcepcion(String mensaje){
        super(mensaje);
    }
    
}
