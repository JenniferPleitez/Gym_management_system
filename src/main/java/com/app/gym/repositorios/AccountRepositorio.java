package com.app.gym.repositorios;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.gym.modelos.Account;
import com.app.gym.modelos.account.AccountSecurityView;

public interface AccountRepositorio extends JpaRepository<Account, Long>{

    Optional<Account> findByIdAccount(Long id);
    
    Optional<Account> findByEmail(String email);

    Optional<Account> findByPublicId(UUID publicId);

    Optional<AccountSecurityView> findSecurityStatusByEmail(String email);

    boolean existsByEmail(String email);
}
