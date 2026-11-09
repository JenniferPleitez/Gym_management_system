package com.app.gym.dtos.login.google;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
    @NotBlank(message = "Google Token is required")
    String idToken
) {}
