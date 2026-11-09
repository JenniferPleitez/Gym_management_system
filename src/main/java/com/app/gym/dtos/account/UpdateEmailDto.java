package com.app.gym.dtos.account;

import jakarta.validation.constraints.Email;

public record UpdateEmailDto(
    @Email(message = "Must be a valid email")
    String email
){}
