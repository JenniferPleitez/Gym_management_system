package com.app.gym.repositorios;

import com.app.gym.modelos.Account;
import com.app.gym.modelos.AccountVerificationToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.List;

public interface AccountVerificationTokenRepositorio extends JpaRepository<AccountVerificationToken, Long> {

    // Buscar token por su hash
    Optional<AccountVerificationToken> findByTokenHash(String tokenHash);

    // Buscar tokens válidos para una cuenta específica
    List<AccountVerificationToken> findByAccountAndUsedFalseAndExpiresAtAfter(Account account, OffsetDateTime now);

    // Eliminar tokens expirados
    void deleteByExpiresAtBefore(OffsetDateTime cutoff);

    @Modifying
    @Query("UPDATE AccountVerificationToken t SET t.used = true WHERE t.account.id = :id AND t.used = false AND t.expiresAt > :now")
    void invalidateAllActiveTokens(Long id, OffsetDateTime now);
}
