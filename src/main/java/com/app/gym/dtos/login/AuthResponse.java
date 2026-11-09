package com.app.gym.dtos.login;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private Long accountId;
    private UUID publicId;
    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Instant expiresAt;
    private List<String> roles;
}