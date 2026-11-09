package com.app.gym.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.auth.RequestPasswordResetRequest;
import com.app.gym.dtos.auth.ResetPasswordRequest;
import com.app.gym.events.EventBuilders;
import com.app.gym.servicios.AccountService;
import com.app.gym.servicios.PasswordResetService;

@Service
public class PasswordResetAppService {
    private final PasswordResetService passwordResetService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AccountService accountService;

    public PasswordResetAppService(PasswordResetService passwordResetService,
                                    ApplicationEventPublisher applicationEventPublisher,
                                    AccountService accountService){
        this.passwordResetService = passwordResetService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.accountService = accountService;
    }

    @Transactional
    public void verifyResetPasswordToken(ResetPasswordRequest resetPasswordRequest){
        String token = resetPasswordRequest.token();
        Long accountId = passwordResetService.getInternalIdByValidToken(token);

        passwordResetService.resetPasswordToken(token, accountId);
        passwordResetService.deleteTokenFromCache(accountId, token);

        accountService.updatePassword(accountId, resetPasswordRequest.newPassword());

        applicationEventPublisher.publishEvent(
            EventBuilders.account()
            .forAccount(accountId)
            .withPassword(resetPasswordRequest.newPassword())
            .buildPasswordReset()
        );
    }

    @Transactional
    public void requestResetPasswordToken(RequestPasswordResetRequest requestPasswordResetRequest){
        String email = requestPasswordResetRequest.email();
        Long id = accountService.verificateAccountStatus(email);
        
        passwordResetService.invalidateAllActiveTokens(id);

        String token = passwordResetService.createPasswordResetToken(email, id);
        passwordResetService.removeOldAndSaveNewToken(id, token);
        
        applicationEventPublisher.publishEvent(
            EventBuilders.accountToken()
            .bySubject(email)
            .withToken(token)
            .buildRequestResetPassword()
        );
    }
}
