package com.app.gym.repositorios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.app.gym.modelos.Account;
import com.app.gym.modelos.PasswordResetToken;

public interface PasswordResetTokenRepositorio extends JpaRepository<PasswordResetToken, Long> {
    
    // Buscar token por su hash
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    // Buscar tokens válidos para una cuenta específica
    public List<PasswordResetToken> findByAccountAndUsedFalseAndExpiresAtAfter(Account account, OffsetDateTime now);

    // Eliminar tokens expirados
    public void deleteByExpiresAtBefore(OffsetDateTime cutoff);

    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.account.id = :id AND t.used = false AND t.expiresAt > :now")
    void invalidateAllActiveTokens(Long id, OffsetDateTime now);
}
