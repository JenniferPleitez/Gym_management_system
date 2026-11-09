package com.app.gym.events.auth.login;

public record LogoutAllEvent(
    Long idAccount,
    String ip,
    String userAgent
){}
