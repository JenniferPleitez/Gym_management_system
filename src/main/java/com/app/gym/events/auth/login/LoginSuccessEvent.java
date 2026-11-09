package com.app.gym.events.auth.login;

public record LoginSuccessEvent(
    Long idAccount,
    String ip,
    String userAgent
){}
