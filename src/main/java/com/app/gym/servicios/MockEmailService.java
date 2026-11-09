package com.app.gym.servicios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
/**
 * Implementación "simulada" de EmailService que imprime en consola.
 * En producción, esto se reemplazarrá por smtp real usando JavaMailSender.
 */
@Service // Registra este bean en Spring
public class MockEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(MockEmailService.class);
    private static final String BASE_URL = "http://localhost:8080/api/auth";

    @Override
    public void sendVerificationEmail(Long id, String token) {
        String verificationLink = String.format("%s/verify-email?token=%s", BASE_URL, token);
        
        log.info("---- SIMULANDO ENVÍO DE CORREO ----");
        log.info("Para la cuenta con id: {}", id);
        log.info("Asunto: Verifica tu cuenta");
        log.info("Cuerpo: Haz clic en el siguiente enlace para verificar tu cuenta:");
        log.info("{}", verificationLink);
        log.info("------------------------------------");
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        String resetLink = String.format("%s/reset-password?token=%s", BASE_URL, token);

        log.info("---- SIMULANDO ENVÍO DE CORREO ----");
        log.info("Para la cuenta con email: {}", email);
        log.info("Asunto: Restablece tu contraseña");
        log.info("Cuerpo: Haz clic en el siguiente enlace para restablecer tu contraseña:");
        log.info("{}", resetLink);
        log.info("------------------------------------");
    }
}
