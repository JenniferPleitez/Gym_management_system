package com.app.gym.utils.validadores;

import java.util.regex.Pattern;

import com.app.gym.excepciones.ContrasenaInvalidaExcepcion;

/*
 * Validador de contraseñas para asegurar que cumplen con los requisitos de seguridad.
 */
public class PasswordValidator {
    private static final Pattern STRONG_PASSWORD =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    public static void validate(String password) {
        if (!STRONG_PASSWORD.matcher(password).matches()) {
            throw new ContrasenaInvalidaExcepcion(
                    "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.");
        }
    }
}
