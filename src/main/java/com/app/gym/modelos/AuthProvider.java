package com.app.gym.modelos;

import java.time.OffsetDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.app.gym.modelos.enums.AuthProviders;

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
@Table(name = "auth_providers")
@Data
public class AuthProvider {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAuthProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private AuthProviders authProvider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @PrePersist
    public void prePersist() {
        if(authProvider == null) {
            authProvider = AuthProviders.LOCAL;
        }

        createdAt = OffsetDateTime.now();
    }
}
