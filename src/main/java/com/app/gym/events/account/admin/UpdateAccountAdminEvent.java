package com.app.gym.events.account.admin;

public record UpdateAccountAdminEvent(
    Long idAccount,
    String email,
    String ip,
    String userAgent
){}

