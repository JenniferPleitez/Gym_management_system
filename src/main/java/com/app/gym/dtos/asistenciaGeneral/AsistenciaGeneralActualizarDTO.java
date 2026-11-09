package com.app.gym.dtos.asistenciaGeneral;

import java.time.LocalDate;
import java.time.LocalTime;

public class AsistenciaGeneralActualizarDTO {

    /*
     * DTO para actualizar la asistencia general de un usuario.
     * Contiene todos los campos necesarios a modificar de una asistencia general.
     */

    private LocalDate fecha;
    private LocalTime horaEntrada;
    private boolean anulada; // Marca la asistencia como anulada (soft-delete) que por defecto el false
    private Integer horarioPorDia;
    private Integer usuario;

    // Getters 

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public Boolean getAnulada() {
        return anulada;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Integer getHorarioPorDia() {
        return horarioPorDia;
    }

    public Integer getUsuario() {
        return usuario;
    }
}
