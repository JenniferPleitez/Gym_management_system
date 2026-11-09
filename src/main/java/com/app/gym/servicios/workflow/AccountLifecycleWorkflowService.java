package com.app.gym.servicios.workflow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.excepciones.EmailSendingException;
import com.app.gym.modelos.enums.ActionLog;
import com.app.gym.servicios.AuditService;
import com.app.gym.servicios.EmailService;
import com.app.gym.servicios.RefreshTokenService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountLifecycleWorkflowService{
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;
    private final EmailService emailService;

    public AccountLifecycleWorkflowService(AuditService auditService,
                                        EmailService emailService,
                                        RefreshTokenService refreshTokenService){
        this.auditService = auditService;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
    }

    public void executeAccountCreation(Long accountId, String token, String ip, String userAgent) {
        try{
            auditService.log(accountId,null, ActionLog.CREATE_USER, ip, userAgent, "Account created by system");
            emailService.sendVerificationEmail(accountId, token);
            auditService.log(accountId, null, ActionLog.SEND_VERIFICATION, ip, userAgent, "Account verification send by system");
        } catch(Exception e){
            log.error("Error sending email to  accound id {}", accountId);
            //auditService.log(accountId, ActionLog.ERROR_SENDING_VERIFICATION_EMAIL, null, null, "An error occurred while the system was attempting to send the email");
            
            throw new EmailSendingException("An error occurred while the system was attempting to send the email");
        }
    }

    public void executeAccountCreationAdmin(Long accountId, String adminEmail, String ip, String userAgent){
        auditService.log(accountId,
                        adminEmail,
                        ActionLog.CREATE_USER, 
                        ip, 
                        userAgent, 
                        "Account created by an admin");
            
        //emailService.sendConfirmAccountCreatedEmail(event.idAccount());
    }
    
    public void executeAccountUpdateByAdmin(Long accountId, String adminEmail, String ip, String userAgent){
        auditService.log(accountId, 
                        adminEmail,
                        ActionLog.UPDATE_USER, 
                        ip, 
                        userAgent, 
                        "Account updated by admin with email: " + adminEmail);
    }

    public void executeUpdateEmail(Long accountId, String token, String ip, String userAgent){
        auditService.log(accountId, null, ActionLog.EMAIL_UPDATE_REQUESTED, ip, userAgent, "Email update requested");
        
        try{
            emailService.sendVerificationEmail(accountId, token);
            auditService.log(accountId, null, ActionLog.SEND_VERIFICATION, ip, userAgent, "Account verification send by system");
        } catch(Exception e){
            log.error("Error sending email to  accound id {}", accountId);
            //auditService.log(accountId, ActionLog.ERROR_SENDING_VERIFICATION_EMAIL, null, null, "An error occurred while the system was attempting to send the email");
            
            throw new EmailSendingException("An error occurred while the system was attempting to send the email");
        }
        
    }

    @Transactional
    public void executeAccountDisabled(Long accountId, String adminEmail, String ip, String userAgent, String details) {
        refreshTokenService.revokeAllByAccount(accountId);
        auditService.log(accountId, adminEmail, ActionLog.ACCOUNT_DISABLED, ip, userAgent, details);
        sendDisabledEmail(accountId);
    }

    @Transactional
    public void executeAccountEnabled(Long accountId, String adminEmail, String ip, String userAgent, String details) {
        auditService.log(accountId, adminEmail, ActionLog.ACCOUNT_ENABLED, ip, userAgent, details);
    }

    @Transactional
    public void executeAccountLocked(Long accountId, String adminEmail, String ip, String userAgent, String details) {
        refreshTokenService.revokeAllByAccount(accountId);
        auditService.log(accountId, adminEmail, ActionLog.ACCOUNT_LOCKED, ip, userAgent, details);
        //sendDisabledEmail(accountId);
    }

    @Transactional
    public void executeAccountUnlocked(Long accountId, String adminEmail, String ip, String userAgent, String details) {
        auditService.log(accountId, adminEmail, ActionLog.ACCOUNT_UNLOCKED, ip, userAgent, details);
    }

    // Método a parte para tomar exception en caso de fallo y que no le haga rollback a la cuenta que fue revocada.
    private void sendDisabledEmail(Long accountId){
        try{
            // emailService.sendAccountDisabledAlert(accountId);
        } catch(Exception e){
            log.error("Error sending email to  accound id {}", accountId);
            //auditService.log(accountId, ActionLog.ERROR_SENDING_VERIFICATION_EMAIL, null, null, "An error occurred while the system was attempting to send the email");
        }
    }
}