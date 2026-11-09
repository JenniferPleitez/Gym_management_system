package com.app.gym.servicios;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.gym.modelos.AuthProvider;
import com.app.gym.modelos.enums.AuthProviders;
import com.app.gym.repositorios.AccountRepositorio;
import com.app.gym.repositorios.AuthProviderRepositorio;

@Service
public class AuthProviderService {
    
    private final AuthProviderRepositorio authProviderRepositorio;
    private final AccountRepositorio accountRepositorio;

    public AuthProviderService(AuthProviderRepositorio authProviderRepositorio, AccountRepositorio accountRepositorio) {
        this.authProviderRepositorio = authProviderRepositorio;
        this.accountRepositorio = accountRepositorio;
    }

    public Optional<AuthProvider> findByProviderAndId(AuthProviders provider, String providerId) {
        return authProviderRepositorio.findByAuthProviderAndProviderId(provider, providerId);
    }

    public AuthProvider createProviderForAccount(Long accountId, AuthProviders provider, String providerId) {
        AuthProvider newProvider = new AuthProvider();
        newProvider.setAccount(accountRepositorio.getReferenceById(accountId));
        newProvider.setAuthProvider(provider);
        newProvider.setProviderId(providerId);

        return authProviderRepositorio.save(newProvider);
    }
}
