package com.app.gym.dtos.asistenciaClaseAerobica;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class AsistenciaClaseAerobicaRequestDTO {

    /** Opcional: si no viene se usará la fecha actual. */
    private LocalDate fecha;

    @NotNull(message = "Debe indicar la clase aeróbica")
    private Integer idClase;

    @NotNull(message = "Debe indicar el usuario")
    private Integer idUsuario;
    
    // Getters y Setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getIdClase() {
        return idClase;
    }

    public void setIdClase(Integer idClase) {
        this.idClase = idClase;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}
