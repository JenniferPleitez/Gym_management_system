package com.app.gym.servicios.workflow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.modelos.enums.ActionLog;
import com.app.gym.servicios.AccountService;
import com.app.gym.servicios.AuditService;
import com.app.gym.servicios.cache.AccountCacheService;
//import com.app.gym.servicios.EmailService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthWorkflowService {
    private final AccountService accountService;
    private final AccountCacheService accountCacheService;
    private final AuditService auditService;
    //private final EmailService emailService;

    public AuthWorkflowService(AccountService accountService,
                                /*EmailService emailService,*/
                                AccountCacheService accountCacheService,
                                AuditService auditService){
        this.accountService = accountService;
        this.auditService = auditService;
        this.accountCacheService = accountCacheService;
        //this.emailService = emailService;
    }

    @Transactional
    public void executeSuccessfulLogin(Long accountId, String ip, String userAgent) {
        accountService.actualizarUltimoLogin(accountId);
        auditService.log(accountId, null, ActionLog.LOGIN, ip, userAgent, "Successful login");
        //emailService.sender();
        try {
                accountCacheService.saveAccountCache(accountService.getAccountById(accountId));
        } catch (Exception e) {
            log.warn("Failed to warm cache for account {}: {}", accountId, e.getMessage(), e);
        }
    }
    
    public void executeFailedLogin(String subject, String ip, String userAgent) {
        auditService.log(subject, ActionLog.LOGIN_FAIL, ip, userAgent, "Error logging in");
        //emailService.sender();
    }

    public void executeLogRotateRefresh(Long accountId, String ip, String userAgent){
        auditService.log(accountId, null, ActionLog.REFRESH_ROTATE, ip, userAgent, "Valid rotate refresh");
    }

    public void executeLogoutAll(Long accountId, String ip, String userAgent){
        auditService.log(accountId, null, ActionLog.LOGOUT_ALL, ip, userAgent, "All sessions were closed");
    }

    public void executeTokenTheft(Long accountId, String ip, String userAgent){
        //emailService.sendSecurityAlert(accountId, "We detected a possible fraudulent login attempt.");

        auditService.log(accountId, null, ActionLog.REFRESH_TOKEN_REUSE, ip, userAgent, 
                        "An attempt was made to use a revoked refresh token. All of the user's tokens have been revoked for security reasons.");
    }

    public void executeRotateParallel(Long accountId, String ip, String userAgent){
        auditService.log(accountId, null, ActionLog.ROTATE_REFRESH_PARALLEL, ip, userAgent, 
                        "Parallel rotation of refresh token detected and allowed.");
    }
}
