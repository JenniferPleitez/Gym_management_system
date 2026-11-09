package com.app.gym.modelos;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class AsistenciaId implements Serializable{

    /*
     * Clase que representa una clave primaria compuesta para la entidad Asistencia.
     * Esta clase implementa Serializable para permitir su uso como clave primaria en JPA.
     */

    private Integer idAsistencia;
    private LocalDate fecha;

    public AsistenciaId() {
    }

    public AsistenciaId(Integer idAsistencia, LocalDate fecha) {
        this.idAsistencia = idAsistencia;
        this.fecha = fecha;
    }

    //Getters y setters
    public Integer getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(Integer idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    // Compara dos objetos VentaId para verificar si son iguales.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AsistenciaId)) return false;
        AsistenciaId asistenciaId = (AsistenciaId) o;
        return idAsistencia.equals(asistenciaId.idAsistencia) && fecha.equals(asistenciaId.fecha);
    }

    // Genera un código hash basado en los atributos de la clase.
    @Override
    public int hashCode() {
        return Objects.hash(idAsistencia, fecha);
    }
}
