package com.app.gym.dtos.account;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AccountUpdateAdminDTO(
    @NotNull(message = "Account ID cannot be null")
    UUID publicId,

    @Email(message = "Invalid email format")
    String accountEmail,
    
    String role
){}