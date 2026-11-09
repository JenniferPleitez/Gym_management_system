package com.app.gym.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.auth.ResendVerificationRequest;
import com.app.gym.dtos.auth.VerifyEmailRequest;
import com.app.gym.events.account.lifecycle.AccountVerifiedEmail;
import com.app.gym.events.account.token.ResendAccountVerificationEvent;
import com.app.gym.seguridad.audit.RequestAuditContext;
import com.app.gym.servicios.AccountService;
import com.app.gym.servicios.AccountVerificationTokenService;

@Service
public class AccountVerificationTokenAppService {
    private final AccountVerificationTokenService accountVerificationTokenService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AccountService accountService;

    public AccountVerificationTokenAppService(AccountVerificationTokenService accountVerificationTokenService,
                                            ApplicationEventPublisher applicationEventPublisher,
                                            AccountService accountService){
        this.accountVerificationTokenService = accountVerificationTokenService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.accountService = accountService;
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest verifyEmailRequest){
        String token = verifyEmailRequest.token();
        Long accountId = accountVerificationTokenService.getInternalIdByValidToken(token);

        accountVerificationTokenService.verifyEmailToken(token, accountId);
        accountVerificationTokenService.deleteCachesAfterVerification(accountId, token);

        accountService.verificarEmail(accountId);

        applicationEventPublisher.publishEvent(new AccountVerifiedEmail(accountId,
                                                                        RequestAuditContext.getIp(), 
                                                                        RequestAuditContext.getUserAgent()
        ));
    }

    @Transactional
    public String resendEmailVerificationToken(ResendVerificationRequest resendVerificationRequest){
        String email = resendVerificationRequest.email();
        Long id = accountService.verificateAccountVerified(email);
        accountService.verificateAccountVerified(email);

        accountVerificationTokenService.invalidateAllActiveToken(id);
        String token = accountVerificationTokenService.createVerificationToken(id, email);
        
        accountVerificationTokenService.removeOldTokenFromCache(id);
        accountVerificationTokenService.saveTokenToCache(id, token);

        applicationEventPublisher.publishEvent(new ResendAccountVerificationEvent(id,
                                                                                token,
                                                                                RequestAuditContext.getIp(), 
                                                                                RequestAuditContext.getUserAgent()
        ));

        return "The verification email has been resent.";
    }
}
