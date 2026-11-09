package com.app.gym.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest (
    @NotBlank(message = "Token cannot be blank")
    String token
) {}