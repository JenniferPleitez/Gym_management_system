package com.app.gym.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest( 
    @NotBlank(message = "El token no puede estar vacío.") 
    String token,

    @NotBlank(message = "La nueva contraseña no puede estar vacía.")
    String newPassword)
{}
