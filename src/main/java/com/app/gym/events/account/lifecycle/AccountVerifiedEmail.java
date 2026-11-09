package com.app.gym.events.account.lifecycle;

public record AccountVerifiedEmail(
    Long idAccount,
    String ip,
    String userAgent
){}
