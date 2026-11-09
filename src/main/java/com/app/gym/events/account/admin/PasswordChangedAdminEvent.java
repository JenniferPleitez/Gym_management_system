package com.app.gym.events.account.admin;

public record PasswordChangedAdminEvent(
    Long idAccount,
    String subject,
    String ip,
    String userAgent
){}
