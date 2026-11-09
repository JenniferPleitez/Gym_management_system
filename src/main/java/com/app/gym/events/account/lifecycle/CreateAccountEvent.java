package com.app.gym.events.account.lifecycle;

public record CreateAccountEvent(
    Long idAccount,
    String token,
    String ip,
    String userAgent
){}
