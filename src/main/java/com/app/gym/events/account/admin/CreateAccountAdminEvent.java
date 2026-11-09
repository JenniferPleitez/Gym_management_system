package com.app.gym.events.account.admin;

public record CreateAccountAdminEvent(
    Long idAccount,
    String adminEmail,
    String ip,
    String userAgent
){}