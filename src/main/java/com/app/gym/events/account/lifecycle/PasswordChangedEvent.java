package com.app.gym.events.account.lifecycle;

public record PasswordChangedEvent(
    Long idAccount,
    String ip,
    String userAgent
){}
