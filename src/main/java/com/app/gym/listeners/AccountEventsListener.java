package com.app.gym.listeners;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.app.gym.events.account.admin.AccountDisabledEvent;
import com.app.gym.events.account.admin.AccountEnabledEvent;
import com.app.gym.events.account.admin.AccountLockedEvent;
import com.app.gym.events.account.admin.AccountUnlockedEvent;
import com.app.gym.events.account.admin.CreateAccountAdminEvent;
import com.app.gym.events.account.admin.PasswordChangedAdminEvent;
import com.app.gym.events.account.admin.UpdateAccountAdminEvent;
import com.app.gym.events.account.lifecycle.AccountVerifiedEmail;
import com.app.gym.events.account.lifecycle.CreateAccountEvent;
import com.app.gym.events.account.lifecycle.PasswordChangedEvent;
import com.app.gym.events.account.lifecycle.ResetPasswordTokenVerifiedEvent;
import com.app.gym.events.account.token.RequestResetPasswordEvent;
import com.app.gym.events.account.token.ResendAccountVerificationEvent;
import com.app.gym.events.account.token.UpdateEmailEvent;
import com.app.gym.servicios.workflow.AccountLifecycleWorkflowService;
import com.app.gym.servicios.workflow.AccountVerificationWorkflowService;
import com.app.gym.servicios.workflow.CacheInvalidationWorklow;
import com.app.gym.servicios.workflow.PasswordResetWorkflowService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AccountEventsListener {
    
    private final AccountVerificationWorkflowService accountVerificationWorkflowService;
    private final PasswordResetWorkflowService passwordResetWorkflowService;
    private final AccountLifecycleWorkflowService accountLifecycleWorkflowService;
    private final CacheInvalidationWorklow cacheInvalidationWorklow;

    public AccountEventsListener(AccountVerificationWorkflowService accountVerificationWorkflowService,
                                PasswordResetWorkflowService passwordResetWorkflowService,
                                AccountLifecycleWorkflowService accountLifecycleWorkflowService,
                                CacheInvalidationWorklow cacheInvalidationWorklow){
        this.accountVerificationWorkflowService = accountVerificationWorkflowService;
        this.passwordResetWorkflowService = passwordResetWorkflowService;
        this.accountLifecycleWorkflowService = accountLifecycleWorkflowService;
        this.cacheInvalidationWorklow = cacheInvalidationWorklow;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAccountDisabled(AccountDisabledEvent event){
        accountLifecycleWorkflowService.executeAccountDisabled(event.idAccount(), event.adminEmail(), event.ip(), event.userAgent(), event.details());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAccountEnabled(AccountEnabledEvent event){
        accountLifecycleWorkflowService.executeAccountEnabled(event.idAccount(), event.adminEmail(), event.ip(), event.userAgent(), event.details());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAccountLocked(AccountLockedEvent event){
        accountLifecycleWorkflowService.executeAccountLocked(event.idAccount(), event.adminEmail(), event.ip(), event.userAgent(), event.details());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAccountUnlocked(AccountUnlockedEvent event){
        accountLifecycleWorkflowService.executeAccountUnlocked(event.idAccount(), event.adminEmail(), event.ip(), event.userAgent(), event.details());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAccountVerified(AccountVerifiedEmail event){
        accountVerificationWorkflowService.executeCompleteVerification(event.idAccount(), event.ip(), event.userAgent());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleResetPasswordVerified(ResetPasswordTokenVerifiedEvent event){
        passwordResetWorkflowService.executePasswordReset(event.idAccount(), event.newPassword(), event.ip(), event.userAgent());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleResendVerification(ResendAccountVerificationEvent event){     
        accountVerificationWorkflowService.executeResendVerification(event.idAccount(), event.token(), event.ip(), event.userAgent());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleSendPasswordReset(RequestResetPasswordEvent event){     
        passwordResetWorkflowService.executePasswordResetRequest(event.email(), event.token(), event.ip(), event.userAgent());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handlePasswordChanged(PasswordChangedEvent event){
        passwordResetWorkflowService.executePasswordChanged(event.idAccount(), event.ip(), event.userAgent());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handlePasswordChangedByAdmin(PasswordChangedAdminEvent event){
        passwordResetWorkflowService.executePasswordChangedAdmin(event.idAccount(), event.subject(), event.ip(), event.userAgent());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleCreateAccount(CreateAccountEvent event){     
        accountLifecycleWorkflowService.executeAccountCreation(event.idAccount(), event.token(), event.ip(), event.userAgent());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleCreateAccountAdmin(CreateAccountAdminEvent event){     
        accountLifecycleWorkflowService.executeAccountCreationAdmin(event.idAccount(), event.adminEmail(), event.ip(), event.userAgent());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleUpdateEmail(UpdateEmailEvent event){
        accountLifecycleWorkflowService.executeUpdateEmail(event.idAccount(), event.token(), event.ip(), event.userAgent());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleUpdateAccountByAdmin(UpdateAccountAdminEvent event){
        accountLifecycleWorkflowService.executeAccountUpdateByAdmin(event.idAccount(), event.email(), event.ip(), event.userAgent());
        cacheInvalidationWorklow.executeCacheInvalidateAndRecache(event.idAccount());
    }
}
