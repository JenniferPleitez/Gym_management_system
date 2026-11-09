package com.app.gym.events.account.token;

public record ResendAccountVerificationEvent(
    Long idAccount,
    String token,
    String ip,
    String userAgent
){}
