package com.app.gym.excepciones;

public class EmailSendingException extends RuntimeException{
    public EmailSendingException(String message){
        super(message);
    }   
}
