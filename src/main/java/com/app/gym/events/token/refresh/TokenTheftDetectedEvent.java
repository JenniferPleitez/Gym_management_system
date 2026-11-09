package com.app.gym.events.token.refresh;

public record TokenTheftDetectedEvent(
    Long idAccount,
    String createdByIp,
    String deviceInfo
){}
