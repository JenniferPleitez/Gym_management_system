package com.app.gym.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.account.AccountCrearDTO;
import com.app.gym.dtos.auth.LogoutRequest;
import com.app.gym.dtos.login.AuthResponse;
import com.app.gym.dtos.login.LoginRequestDTO;
import com.app.gym.events.EventBuilders;
import com.app.gym.excepciones.AppSecurityException;
import com.app.gym.modelos.Account;
import com.app.gym.servicios.AccountVerificationTokenService;
import com.app.gym.servicios.AuthService;
import com.app.gym.utils.CurrentUser;

@Service
public class AuthAppService {
    private final AuthService authService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AccountVerificationTokenService accountVerificationTokenService;
    private final CurrentUser currentUser;

    public AuthAppService(AuthService authService,
                        ApplicationEventPublisher applicationEventPublisher,
                        AccountVerificationTokenService accountVerificationTokenService,
                        CurrentUser currentUser){
        this.authService = authService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.accountVerificationTokenService = accountVerificationTokenService;
        this.currentUser = currentUser;
    }

    @Transactional
    public AuthResponse login(LoginRequestDTO loginRequestDTO){
        try{
            AuthResponse authResult = authService.login(loginRequestDTO);

            applicationEventPublisher.publishEvent(
                EventBuilders.Authentication()
                .forAccount(authResult.getAccountId())
                .buildLoginSuccess()
            );

            return authResult;
        } 
        catch(BadCredentialsException ex){

            applicationEventPublisher.publishEvent(
                EventBuilders.Authentication()
                .withSubject(loginRequestDTO.email())
                .buildLoginFailed()
            );

            throw new AppSecurityException("Oops! It looks like an error has occurred. Incorrect email or password.");
        }
    }

    @Transactional
    public AuthResponse refresh(String token) {
        AuthResponse refreshResponse = authService.refresh(token);

        applicationEventPublisher.publishEvent(
            EventBuilders.refreshToken()
            .forAccount(refreshResponse.getAccountId())
            .buildRefreshRotate()
        );
                                                            
        return refreshResponse;
    }

    @Transactional
    public Account register(AccountCrearDTO accountRequestDto) {
        Account createdAccount = authService.register(accountRequestDto);

        String token = accountVerificationTokenService.createVerificationToken(createdAccount.getIdAccount(), accountRequestDto.email());
        accountVerificationTokenService.saveTokenToCache(createdAccount.getIdAccount(), token);

        applicationEventPublisher.publishEvent(
            EventBuilders.accountToken()
            .forAccount(createdAccount.getIdAccount())
            .withToken(token)
            .buildCreateAccount()
        );

        return createdAccount;
    }

    @Transactional
    public void logout(LogoutRequest logoutRequest) {
        authService.logout(logoutRequest.refreshToken());
    }

    @Transactional
    public void logoutAll() {
        Long id = currentUser.getAccountId();
        authService.logoutAll(id);
        authService.deleteAccountTokenVersion(id);

        applicationEventPublisher.publishEvent(
            EventBuilders.Authentication()
            .forAccount(id)
            .buildLogoutAll()
        );
    }
}
