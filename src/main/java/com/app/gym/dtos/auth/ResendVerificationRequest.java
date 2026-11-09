package com.app.gym.dtos.auth;

import jakarta.validation.constraints.Email;

public record ResendVerificationRequest(
    @Email(message = "Cannot be null")
    String email
) {}