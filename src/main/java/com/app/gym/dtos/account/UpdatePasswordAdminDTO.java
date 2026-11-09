package com.app.gym.dtos.account;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePasswordAdminDTO(
    @NotNull(message = "Cannot be null")
    UUID publicId,

    @NotBlank(message = "Cannot be blank")
    String newPassword
) {}
