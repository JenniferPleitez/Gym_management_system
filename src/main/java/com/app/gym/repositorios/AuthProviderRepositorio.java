package com.app.gym.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.gym.modelos.AuthProvider;
import com.app.gym.modelos.enums.AuthProviders;

public interface AuthProviderRepositorio extends JpaRepository<AuthProvider, Long> {
    Optional<AuthProvider> findByAuthProviderAndProviderId(AuthProviders provider, String providerId);

    Optional<AuthProvider> findByAuthProvider(String provider);
}
