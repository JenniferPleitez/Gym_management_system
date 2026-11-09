package com.app.gym.repositorios;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.gym.modelos.Membresia;

public interface MembresiaRepositorio extends JpaRepository<Membresia, Integer> {

    // Método para buscar una membresía por su ID.
    public Optional<Membresia> findByIdMembresia (Integer id);

    // Método para buscar una membresía por su nombre.
    public Optional<Membresia> findByNombre (String nombre);
}
