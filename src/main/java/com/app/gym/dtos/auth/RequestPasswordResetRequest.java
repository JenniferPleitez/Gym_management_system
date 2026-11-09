package com.app.gym.dtos.auth;

import jakarta.validation.constraints.Email;

public record RequestPasswordResetRequest(
    @Email(message = "Invalid email format") 
    String email
) {}