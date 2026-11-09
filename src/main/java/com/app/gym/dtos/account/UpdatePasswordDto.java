package com.app.gym.dtos.account;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordDto(
    @NotBlank(message = "Cannot be blank")
    String newPassword
){}