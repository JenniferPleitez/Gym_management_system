package com.app.gym.modelos;

import java.time.OffsetDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.app.gym.modelos.enums.ActionLog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAuditLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = true)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "action_log", nullable = false, length = 50)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ActionLog actionLog; // RESET_PASSWORD, VERIFY_EMAIL, RESEND_TOKEN, LOGIN_FAIL, etc.

    @Column(length = 50)
    private String subject; // EX: Email, Username, etc... 

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @JdbcTypeCode(SqlTypes.INET)
    @Column(length = 50, columnDefinition = "inet")
    private String ip;

    @Column(length = 200)
    private String userAgent;

    @Column(length = 255)
    private String details;

    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now();
    }
}
