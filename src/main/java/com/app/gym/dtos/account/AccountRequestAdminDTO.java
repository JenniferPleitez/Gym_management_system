package com.app.gym.dtos.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AccountRequestAdminDTO(
    @Email(message = "Email must be in a correct format")
    String email,

    @NotBlank(message = "Password cannot be blank")
    String password,

    @NotBlank(message = "Role cannot be blank")
    String role
){}
