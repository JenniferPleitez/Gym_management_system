package com.app.gym.dtos.login;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleAuthResult {
    private Long accountId;
    private String providerId;
    private boolean isProfileCompleted;
    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Instant expiresAt;
    private List<String> roles;
}
