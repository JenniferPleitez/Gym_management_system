package com.app.gym.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.gym.modelos.ClaseAerobica;

public interface ClaseAerobicaRepositorio extends JpaRepository<ClaseAerobica, Integer> {
    
}
