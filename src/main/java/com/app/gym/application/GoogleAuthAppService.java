package com.app.gym.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.login.GoogleAuthResult;
import com.app.gym.events.EventBuilders;
import com.app.gym.excepciones.AppSecurityException;
import com.app.gym.servicios.GoogleAuthService;
import com.app.gym.utils.cookie.AuthCookieManager;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class GoogleAuthAppService {
    private final GoogleAuthService googleAuthService;
    private final AuthCookieManager authCookieManager;
    private final ApplicationEventPublisher applicationEventPublisher;

    public GoogleAuthAppService(GoogleAuthService googleAuthService, 
                                AuthCookieManager authCookieManager, 
                                ApplicationEventPublisher applicationEventPublisher) {
        this.googleAuthService = googleAuthService;
        this.authCookieManager = authCookieManager;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public GoogleAuthResult authenticateWithGoogle(String idTokenString, HttpServletResponse response) {
        try{
            GoogleAuthResult authResult = googleAuthService.authenticateWithGoogle(idTokenString);

            authCookieManager.setRefreshTokenCookie(response, authResult.getRefreshToken());

            applicationEventPublisher.publishEvent(
                EventBuilders.Authentication()
                .forAccount(authResult.getAccountId())
                .buildLoginSuccess()
            );

            return authResult;
        } catch (Exception e) {
            applicationEventPublisher.publishEvent(
                EventBuilders.Authentication()
                .withSubject("Google Authentication")
                .buildLoginFailed()
            );

            throw new AppSecurityException("Oops! It looks like an error has occurred. Unable to authenticate with Google.");
        }
    }
}
