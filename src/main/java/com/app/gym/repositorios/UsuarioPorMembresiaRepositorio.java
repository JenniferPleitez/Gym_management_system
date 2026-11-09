package com.app.gym.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.gym.modelos.UsuarioPorMembresia;

public interface UsuarioPorMembresiaRepositorio extends JpaRepository<UsuarioPorMembresia, String> {
    
}
