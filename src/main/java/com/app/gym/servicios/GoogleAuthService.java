package com.app.gym.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.account.AccountCreateWithProviderDTO;
import com.app.gym.dtos.login.GoogleAuthResult;
import com.app.gym.modelos.Account;
import com.app.gym.modelos.AuthProvider;
import com.app.gym.modelos.enums.AuthProviders;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;


@Service
public class GoogleAuthService {
    private final AccountService accountService;
    private final TokenService tokenService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final RefreshTokenService refreshTokenService;
    private final AuthProviderService authProviderService;

    public GoogleAuthService(AccountService accountService, 
                                TokenService tokenService,
                                GoogleIdTokenVerifier googleIdTokenVerifier,
                                RefreshTokenService refreshTokenService,
                                AuthProviderService authProviderService) {
        this.accountService = accountService;
        this.tokenService = tokenService;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.refreshTokenService = refreshTokenService;
        this.authProviderService = authProviderService;
    }

    @Transactional
    public GoogleAuthResult authenticateWithGoogle(String idTokenString) {
        GoogleIdToken idToken = verifyGoogleToken(idTokenString);   
        GoogleIdToken.Payload payload = idToken.getPayload();

        String email = payload.getEmail();
        String googleId = payload.getSubject();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        Optional<AuthProvider> existingProvider = authProviderService.findByProviderAndId(AuthProviders.GOOGLE, googleId);

        Account account;

        if(existingProvider.isPresent()) {
            account = existingProvider.get().getAccount();
        } else {
            account = accountService.findOptionalByEmail(email)
                    .orElseGet(() -> accountService.createAccountWithProvider(new AccountCreateWithProviderDTO(email)));

            authProviderService.createProviderForAccount(account.getIdAccount(), AuthProviders.GOOGLE, googleId);
        }

        return generateAuthResult(account);
    }

    private GoogleIdToken verifyGoogleToken(String idTokenString) {
        try{
            GoogleIdToken idToken = googleIdTokenVerifier.verify(idTokenString);

            if (idToken == null) {
                throw new IllegalArgumentException("Invalid ID token.");
            } 

            return idToken;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to verify ID token.", e);
        }
    }

    private GoogleAuthResult generateAuthResult(Account account) {
        List<String> roles = account.getRoles().stream()
                .map(role -> role.getRoleName())
                .toList();

        String accessToken = tokenService.generateAccessToken(account.getPublicId(), account.getTokenVersion(), roles);
        String rawRefresh = refreshTokenService.createRefreshToken(account);

        GoogleAuthResult response = new GoogleAuthResult();
        response.setAccountId(account.getIdAccount());
        response.setProfileCompleted(account.isProfileCompleted());
        response.setToken(accessToken);
        response.setRefreshToken(rawRefresh);
        response.setExpiresAt(tokenService.getExpiresAt());
        response.setRoles(roles);

        return response;
    }
}
