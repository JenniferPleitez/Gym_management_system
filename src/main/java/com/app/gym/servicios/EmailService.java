package com.app.gym.servicios;

/*
 * Servicio de ejemplo para enviar correos electrónicos. Es solo un mock que imprime por consola
 * los correos que "envía".  En un entorno real, esto se implementaría usando JavaMailSender.
 */
public interface EmailService {

    /**
     * Envía un correo de verificación de cuenta.
     * @param account La cuenta del usuario.
     * @param token El token real (no hasheado) para incluir en el enlace.
     */
    void sendVerificationEmail(Long id, String token);

    /**
     * Envía un correo para restablecer la contraseña.
     * @param account La cuenta del usuario.
     * @param token El token real (no hasheado) para incluir en el enlace.
     */
    void sendPasswordResetEmail(String email, String token);
}