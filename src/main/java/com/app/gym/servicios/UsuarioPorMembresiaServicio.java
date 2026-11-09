package com.app.gym.servicios;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.gym.repositorios.UsuarioPorMembresiaRepositorio;
import com.app.gym.utils.ListUtils;
import com.app.gym.modelos.UsuarioPorMembresia;

/**
 * Servicio de negocio para operaciones sobre Usuarios por cada Membresia.
 * Encapsula la lógica de obtención de datos desde el repositorio.
 */

@Service
public class UsuarioPorMembresiaServicio {
    
    private final UsuarioPorMembresiaRepositorio usuarioPorMembresiaRepositorio;

    public UsuarioPorMembresiaServicio(UsuarioPorMembresiaRepositorio usuarioPorMembresiaRepositorio) {
        this.usuarioPorMembresiaRepositorio = usuarioPorMembresiaRepositorio;
    }

    // Obtener todos los usuarios por membresias
    public List<UsuarioPorMembresia> obtenerUsuariosPorMembresias() {
        return ListUtils.emptyIfNull(usuarioPorMembresiaRepositorio.findAll());
    }
}
