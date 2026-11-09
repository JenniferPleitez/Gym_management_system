package com.app.gym.events.account.admin;

public record AccountDisabledEvent(
    Long idAccount,
    String adminEmail,
    String ip,
    String userAgent,
    String details
){}
