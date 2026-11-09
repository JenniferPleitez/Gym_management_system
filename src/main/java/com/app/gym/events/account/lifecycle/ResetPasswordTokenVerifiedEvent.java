package com.app.gym.events.account.lifecycle;

public record ResetPasswordTokenVerifiedEvent(
    Long idAccount,
    String newPassword,
    String ip,
    String userAgent
){}
