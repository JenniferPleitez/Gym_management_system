package com.app.gym.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;

/*
 * Esta clase se encarga de generar un token seguro
 * para ser usado al momento de verificar la cuenta de un usuario
 */

public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public static String generateSecureToken() {
        byte[] randomBytes = new byte[32]; // 256 bits de entropía
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
