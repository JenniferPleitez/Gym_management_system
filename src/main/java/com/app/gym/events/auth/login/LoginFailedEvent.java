package com.app.gym.events.auth.login;

public record LoginFailedEvent(
    String subject,
    String ip,
    String userAgent
){}
