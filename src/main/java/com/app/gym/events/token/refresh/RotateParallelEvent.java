package com.app.gym.events.token.refresh;

public record RotateParallelEvent(
    Long idAccount,
    String createdByIp,
    String deviceInfo
){}