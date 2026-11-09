package com.app.gym.events.account.token;

public record RequestResetPasswordEvent(
    String email,
    String token,
    String ip,
    String userAgent
){}