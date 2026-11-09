package com.app.gym.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.app.gym.modelos.RefreshToken;

public interface RefreshTokenRepositorio extends JpaRepository<RefreshToken, Integer>{
    
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Revocar todos los tokens de un usuario (Logout global / Cambio de contraseña)
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.account.id = :accountId")
    void revokeAllByAccount(Long accountId);

    @Query("SELECT r FROM RefreshToken r WHERE r.account.idAccount = :accountId ORDER BY r.createdAt DESC")
    List<RefreshToken> findLatestByAccount(Long accountId, Pageable pageable);
}
