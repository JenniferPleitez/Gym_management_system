package com.app.gym.events.account.admin;

public record AccountUnlockedEvent(
    Long idAccount,
    String adminEmail,
    String ip,
    String userAgent,
    String details
){}
