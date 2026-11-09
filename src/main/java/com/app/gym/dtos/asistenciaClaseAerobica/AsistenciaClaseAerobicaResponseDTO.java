package com.app.gym.dtos.asistenciaClaseAerobica;

import java.time.LocalDate;

import com.app.gym.dtos.usuario.UsuarioSimpleDTO;

public class AsistenciaClaseAerobicaResponseDTO {

    /*
     * DTO para representar la respuesta de una asistencia a una clase aeróbica.
     * Contiene información sobre la asistencia, la clase aeróbica y el usuario.
     */
    
    private Integer idAsistenciaAerobica;
    private LocalDate fecha;
    private ClaseAerobicaSimpleDTO idClase;
    private UsuarioSimpleDTO usuario;
    private Boolean anulada;

    // Getters y Setters
    public Integer getIdAsistenciaAerobica() {
        return idAsistenciaAerobica;
    }
    public void setIdAsistenciaAerobica(Integer idAsistenciaAerobica) {
        this.idAsistenciaAerobica = idAsistenciaAerobica;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public ClaseAerobicaSimpleDTO getIdClase() {
        return idClase;
    }
    public void setIdClase(ClaseAerobicaSimpleDTO idClase) {
        this.idClase = idClase;
    }
    public UsuarioSimpleDTO getUsuario() {
        return usuario;
    }
    public void setUsuario(UsuarioSimpleDTO usuario) {
        this.usuario = usuario;
    }
    public Boolean getAnulada() {
        return anulada;
    }
    public void setAnulada(Boolean anulada) {
        this.anulada = anulada;
    }
}
