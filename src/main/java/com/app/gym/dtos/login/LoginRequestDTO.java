package com.app.gym.dtos.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record LoginRequestDTO(
    @Email(message = "Email must be in a valid format.")
    String email,

    @NotBlank(message = "Password cannot be blank")
    String password
){}
