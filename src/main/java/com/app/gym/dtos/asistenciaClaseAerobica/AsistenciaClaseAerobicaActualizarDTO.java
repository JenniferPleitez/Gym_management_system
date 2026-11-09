package com.app.gym.dtos.asistenciaClaseAerobica;

import java.time.LocalDate;

public class AsistenciaClaseAerobicaActualizarDTO {
    
    private LocalDate fecha;
    private Boolean anulada;
    private Integer idClase;
    private Integer idUsuario;

    // Getters and Setters
    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public Boolean getAnulada() {
        return anulada;
    }
    public void setAnulada(Boolean anulada) {
        this.anulada = anulada;
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
