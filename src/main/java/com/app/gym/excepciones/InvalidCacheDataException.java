package com.app.gym.excepciones;

public class InvalidCacheDataException extends RuntimeException{
    public InvalidCacheDataException(String message){
        super(message);
    }
}