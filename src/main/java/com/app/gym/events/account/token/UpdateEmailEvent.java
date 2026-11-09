package com.app.gym.events.account.token;

public record UpdateEmailEvent(
    Long idAccount,
    String token,
    String ip,
    String userAgent
){}
