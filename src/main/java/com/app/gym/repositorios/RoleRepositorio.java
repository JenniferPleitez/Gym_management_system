package com.app.gym.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.gym.modelos.Role;

public interface RoleRepositorio extends JpaRepository<Role, Long> {
    public Optional<Role> findByRoleName(String roleName);
}
