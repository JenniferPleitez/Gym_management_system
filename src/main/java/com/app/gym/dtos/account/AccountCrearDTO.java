package com.app.gym.dtos.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AccountCrearDTO(
    @Email(message = "Must be in a valid format")
    String email,

    @NotBlank(message = "Cannot be blank")
    String password
) {}
