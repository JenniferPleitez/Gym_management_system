package com.app.gym.dtos.account;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountResponseDTO(
    @NotNull(message = "Cannot be null")
    Long idAccount,

    @NotBlank(message = "Cannot be blank")
    String publicId,

    @NotBlank(message = "Cannot be blank")
    String email,

    @NotNull(message = "Cannot be null")
    Boolean enabled,

    @NotNull(message = "Cannot be null")
    Boolean locked,

    @NotNull(message = "Cannot be null")
    Boolean emailVerified,

    @NotNull(message = "Cannot be blank")
    List<String> roles
) {}