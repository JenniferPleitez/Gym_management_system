package com.app.gym.dtos.account;

import jakarta.validation.constraints.Email;

public record AccountCreateWithProviderDTO(
    @Email(message = "Must be in a valid format")
    String email
){}
