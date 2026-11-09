package com.app.gym.utils.hashers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.app.gym.excepciones.AppSecurityException;

/**
 * Utilidad para crear un hash rápido y seguro de un token usando HMAC-SHA256.
 * Esto se usa para almacenar una "prueba" del token en la base de datos
 * sin almacenar el token real, que solo vive en Redis.
 */
@Component
public class TokenHasher {
    private final SecretKeySpec keySpec;

    public TokenHasher(@Value("${TOKEN_HMAC_KEY}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new AppSecurityException("Token HMAC key no está configurado");
        }

        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.keySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String hashToken(String token) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] macBytes = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(macBytes);
        } catch (Exception e) {
            throw new AppSecurityException("Error al hashear token con HMAC", e);
        }
    }
}