package com.app.gym.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "Token cannot be blank") 
    String refreshToken
) {}