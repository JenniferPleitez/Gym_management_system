package com.app.gym.events.token.refresh;

public record TokensRevokedEvent(
    Long idAccount,
    String ip,
    String userAgent
){}
