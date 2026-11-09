package com.app.gym.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.gym.modelos.AuditLog;

public interface AuditLogRepositorio extends JpaRepository<AuditLog, Long> {
    
}
