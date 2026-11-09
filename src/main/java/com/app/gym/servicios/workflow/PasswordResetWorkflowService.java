package com.app.gym.servicios.workflow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.modelos.enums.ActionLog;
import com.app.gym.servicios.AuditService;
import com.app.gym.servicios.EmailService;
import com.app.gym.servicios.RefreshTokenService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PasswordResetWorkflowService {
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;
    private final EmailService emailService;

    public PasswordResetWorkflowService(AuditService auditService,
                                        EmailService emailService,
                                        RefreshTokenService refreshTokenService){
        this.auditService = auditService;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
    }

    
    @Transactional
    public void executePasswordReset(Long accountId, String newPassword, String ip, String userAgent) {
        refreshTokenService.revokeAllByAccount(accountId);
        auditService.log(accountId, null, ActionLog.RESET_PASSWORD, ip, userAgent, "Password changed by user via email");
        sendPasswordResetEmail(accountId);
    }

    public void executePasswordResetRequest(String email, String token, String ip, String userAgent){
        auditService.log(email, 
                        ActionLog.REQUEST_RESET_PASSWORD, 
                        ip, 
                        userAgent, 
                        "Request reset password by user with email: " + email);

        emailService.sendPasswordResetEmail(email, token);
    }

    @Transactional
    public void executePasswordChanged(Long accountId, String ip, String userAgent){
        refreshTokenService.revokeAllByAccount(accountId);
        auditService.log(accountId, 
                        null,
                        ActionLog.RESET_PASSWORD, 
                        ip, 
                        userAgent, 
                        "Password changed by user with account ID: " + accountId);
        
        sendPasswordChangedEmail(accountId);
    }

    @Transactional
    public void executePasswordChangedAdmin(Long accountId, String adminSubject, String ip, String userAgent){
        refreshTokenService.revokeAllByAccount(accountId);

        sendPasswordChangedEmail(accountId);

        auditService.log(accountId,
                        adminSubject, // Email of the admin who changed the password
                        ActionLog.RESET_PASSWORD_ADMIN, 
                        ip, 
                        userAgent, 
                        "Password changed by admin for account ID: " + accountId);
    }

    // Si el email falla, el flujo sigue sin afectar el cambio de contraseña
    private void sendPasswordResetEmail(Long accoundId){
        try {
            ////emailService.sendPasswordResetConfirmationEmail(accountId); ENVIAR MENSAJE INDICANDO QUE SE CAMBIÓ LA CONTRA CORRECTAMENTE
        } catch (Exception e) {
            log.error("Error sending email to {}", accoundId);
            //auditService.log(accountId, ActionLog.ERROR_SENDING_CONFIRMATION_EMAIL, null, null, "An error occurred while the system was attempting to send the email");
        }
    }

    // Si el email falla, el flujo sigue sin afectar el cambio de contraseña
    private void sendPasswordChangedEmail(Long accoundId){
        try {
            ////emailService.sendPasswordChangedConfirmationEmail(accountId); ENVIAR MENSAJE INDICANDO QUE SE CAMBIÓ LA CONTRA CORRECTAMENTE
        } catch (Exception e) {
            log.error("Error sending email to {}", accoundId);
            //auditService.log(accountId, ActionLog.ERROR_SENDING_CONFIRMATION_EMAIL, null, null, "An error occurred while the system was attempting to send the email");
        }
    }

}
