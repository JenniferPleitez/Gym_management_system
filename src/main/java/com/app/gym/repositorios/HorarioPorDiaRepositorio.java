package com.app.gym.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.gym.modelos.HorarioPorDia;

public interface HorarioPorDiaRepositorio extends JpaRepository<HorarioPorDia, Integer>{
    
    // Método para buscar un horario por su ID.
    public Optional<HorarioPorDia> findByIdHorario(Integer id);
}
