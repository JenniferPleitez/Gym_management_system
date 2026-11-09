package com.app.gym.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.app.gym.modelos.properties.GoogleAuthProperties;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@Configuration
public class GoogleAuthConfig {
    
    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(GoogleAuthProperties properties) {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(properties.getClientId()))
                .setIssuer("https://accounts.google.com")
                .build();
    }
}
