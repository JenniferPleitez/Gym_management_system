package com.app.gym.events.token.refresh;

public record RefreshRotatedEvent(
    Long idAccount,
    String ip,
    String userAgent
){}
