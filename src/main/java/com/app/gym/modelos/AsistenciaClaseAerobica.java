package com.app.gym.modelos;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Asistencia_clases_aerobicas")
public class AsistenciaClaseAerobica {
    
    /*
     * Esta clase representa la asistencia a clases aeróbicas en el sistema de gestión de gimnasio.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia_aerobica", nullable = false)
    private Integer idAsistenciaClaseAerobica;

    @Column(nullable = false)
    private boolean anulada = false; // Marca si la asistencia está anulada (soft-delete)

    @Column(nullable = false)
    private LocalDate fecha;

    // Relación con la entidad ClaseAerobica.
    @ManyToOne
    @JoinColumn(name = "id_clase", referencedColumnName = "id_clase", nullable = false)
    private ClaseAerobica claseAerobica;

    //Relación con la entidad Usuario.
    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    public AsistenciaClaseAerobica() {
    }

    // Getters y Setters
    public Integer getIdAsistenciaClaseAerobica() {
        return idAsistenciaClaseAerobica;
    }

    public void setIdAsistenciaClaseAerobica(Integer idAsistenciaClaseAerobica) {
        this.idAsistenciaClaseAerobica = idAsistenciaClaseAerobica;
    }

    public ClaseAerobica getClaseAerobica() {
        return claseAerobica;
    }

    public void setClaseAerobica(ClaseAerobica claseAerobica) {
        this.claseAerobica = claseAerobica;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean getAnulada() {
        return anulada;
    }

    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    // Método toString para representar la asistencia a clase aeróbica
    @Override
    public String toString() {
        return "AsistenciaClaseAerobica{" +
                "idAsistenciaClaseAerobica=" + idAsistenciaClaseAerobica +
                ", fecha=" + fecha +
                ", anulada=" + anulada +
                ", claseAerobica=" + claseAerobica +
                ", usuario=" + usuario +
                '}';
    }
}
