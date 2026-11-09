package com.app.gym.dtos.usuario;

import java.time.LocalDate;

public record UsuarioActualizarDTO(
    String nombre,
    String apellido,
    String fechaNacimiento,
    LocalDate fechaInicioMembresia,
    Boolean anulada,
    Integer membresia,
    Integer account
){}
