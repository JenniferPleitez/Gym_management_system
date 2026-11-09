package com.app.gym.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.modelos.Account;
import com.app.gym.modelos.AuditLog;
import com.app.gym.modelos.enums.ActionLog;
import com.app.gym.repositorios.AuditLogRepositorio;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuditService implements AuditServiceI{

    private final AuditLogRepositorio auditRepositorio;
    private final EntityManager entityManager;

    public AuditService(AuditLogRepositorio auditRepositorio, EntityManager entityManager) {
        this.auditRepositorio = auditRepositorio;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(Long accountId, String subject, ActionLog action, String ip, String userAgent, String details) {
        createLog(accountId, subject, action, ip, userAgent, details);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String subject, ActionLog action, String ip, String userAgent, String details) {
        createLog(subject, action, ip, userAgent, details);
    }

    @Override
    @Transactional
    public void transactionalLog(Long accountId, String subject, ActionLog action, String ip, String userAgent, String details) {
        createLog(accountId, subject, action, ip, userAgent, details);
    }

    @Override
    @Transactional
    public void transactionalLog(String subject, ActionLog action, String ip, String userAgent, String details) {
        createLog(subject, action, ip, userAgent, details);
    }

    private void createLog(Long accountId, String subject, ActionLog action, String ip, String userAgent, String details) {
        AuditLog log = new AuditLog();
        log.setAccount(entityManager.getReference(Account.class, accountId));
        log.setSubject(subject);
        log.setActionLog(action);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setDetails(details);
        auditRepositorio.save(log);
    }

    private void createLog(String subject, ActionLog action, String ip, String userAgent, String details) {
        AuditLog log = new AuditLog();
        log.setAccount(null);
        log.setSubject(subject);
        log.setActionLog(action);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setDetails(details);
        auditRepositorio.save(log);
    }
}
