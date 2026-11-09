package com.app.gym.dtos.asistenciaGeneral;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public class AsistenciaRequestDTO {
    
    /*
     * DTO para la solicitud de asistencia a una clase aeróbica.
     * Contiene los campos necesarios para registrar (create) la asistencia de un usuario a una clase específica.
     */

    /** Opcional: si no viene se usará la fecha actual. */
    private LocalDate fecha;

    /** Opcional: si no viene se usará la hora actual. */
    private LocalTime horaEntrada;

    @NotNull(message = "Debe indicar el horario")
    private Integer idHorario;

    @NotNull(message = "Debe indicar el usuario")
    private Integer idUsuario;

    // Getters y Setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public Integer getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Integer idHorario) {
        this.idHorario = idHorario;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    

}
