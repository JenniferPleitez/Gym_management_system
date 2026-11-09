package com.app.gym.servicios.workflow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.modelos.enums.ActionLog;
import com.app.gym.servicios.AuditService;
import com.app.gym.servicios.EmailService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountVerificationWorkflowService {
    private final AuditService auditService;
    private final EmailService emailService;

    public AccountVerificationWorkflowService(AuditService auditService,
                                            EmailService emailService){
        this.auditService = auditService;
        this.emailService = emailService;
    }

    /**
     * Flujo completo de verificación de cuenta.
     * Se ejecuta después de que el token ha sido validado exitosamente.
     * Está hecho así para que todas las operaciones están en una misma transacción (porque sino no efectua el update a la DB verificando la cuenta).
     */
    @Transactional
    public void executeCompleteVerification(Long idAccount, String ip, String userAgent){
            sendConfirmationByEmail(idAccount);

            auditService.log(idAccount,
                            null,
                            ActionLog.VERIFY_EMAIL, 
                            ip, 
                            userAgent, 
                            "Account verified by system.");
    }

    public void executeResendVerification(Long idAccount, String token, String ip, String userAgent){
        emailService.sendVerificationEmail(idAccount, token);

        auditService.log(idAccount,
                        null, 
                        ActionLog.RESEND_TOKEN, 
                        ip, 
                        userAgent, 
                        "Verification token resend by system");
    }

    // Si el email falla, el flujo sigue sin afectar el cambio de contraseña
    private void sendConfirmationByEmail(Long idAccount) {
        try{
            // emailService.sendVerificationConfirmation(account.getIdAccount());
        } catch (Exception e)
        {
            log.error("Error sending email by systems to account with ID: {}", idAccount);
            //auditService.log(accountId, ActionLog.ERROR_SENDING_EMAIL, null, null, "An error occurred while the system was attempting to send the email");
        }
    }
}
