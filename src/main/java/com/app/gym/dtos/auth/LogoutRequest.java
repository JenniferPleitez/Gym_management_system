package com.app.gym.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
    @NotBlank(message = "Token cannot be blank") 
    String refreshToken
) {}