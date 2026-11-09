package com.app.gym.servicios;

import com.app.gym.modelos.enums.ActionLog;

public interface AuditServiceI {

    void log(Long accountId, String subject, ActionLog action, String ip, String userAgent, String details);

    void log(String subject, ActionLog action, String ip, String userAgent, String details);

    void transactionalLog(Long accountId, String subject, ActionLog action, String ip, String userAgent, String details);

    void transactionalLog(String subject, ActionLog action, String ip, String userAgent, String details);
}
