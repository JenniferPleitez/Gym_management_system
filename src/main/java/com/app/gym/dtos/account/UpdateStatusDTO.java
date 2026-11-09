package com.app.gym.dtos.account;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusDTO(
    @NotNull(message = "Cannot be null")
    UUID publicId,

    @NotBlank(message = "Cannot be blank")
    String reason,

    @NotNull(message = "Cannot be null")
    Boolean enabled
){}
