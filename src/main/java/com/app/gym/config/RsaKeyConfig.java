package com.app.gym.config;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RsaKeyConfig {
    
    @Value("${JWT_PRIVATE_KEY}")
    private String jwtPrivateKey;

    @Value("${JWT_PUBLIC_KEY}")
    private String jwtPublicKey;

    @Bean
    public RSAPublicKey rsaPublicKey(){
        try {
            byte[] decoded = Base64.getDecoder().decode(jwtPublicKey);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar la clave pública RSA", e);
        }
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey(){
        try{
            byte[] decoded = Base64.getDecoder().decode(jwtPrivateKey);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar la clave privada RSA", e);
        }
    }
}