package com.app.gym.modelos;

import jakarta.persistence.GeneratedValue;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "clases_aerobicas")
public class ClaseAerobica {

    //Clase que representa una clase aeróbica en el sistema de gestión de gimnasio.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id_clase", nullable = false)
    private Integer idClase; 

    // Capacidad de la clase, es decir, cuántas personas pueden asistir a la clase
    @Column(name = "capacidad")
    private Integer capacidad;

    // Hora de inicio y fin de la clase
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    //Relación con un horario por día
    @ManyToOne
    @JoinColumn(name = "id_horario", referencedColumnName = "id_horario", nullable = false)
    private HorarioPorDia horarioPorDia;

    // Relación con la entidad AsistenciaClaseAerobica, que representa las asistencias a esta clase aeróbica
    @OneToMany(mappedBy = "claseAerobica") 
    private List<AsistenciaClaseAerobica> asistenciaClaseAerobica; 

    public ClaseAerobica() {
    }

    // Getters y Setters
    public Integer getIdClase() {
        return idClase;
    }

    public void setIdClase(Integer idClase) {
        this.idClase = idClase;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public HorarioPorDia getHorarioPorDia() {
        return horarioPorDia;
    }

    public void setHorarioPorDia(HorarioPorDia horarioPorDia) {
        this.horarioPorDia = horarioPorDia;
    }

    public List<AsistenciaClaseAerobica> getAsistenciaClaseAerobica() {
        return asistenciaClaseAerobica;
    }

    public void setAsistenciaClaseAerobica(List<AsistenciaClaseAerobica> asistenciaClaseAerobica) {
        this.asistenciaClaseAerobica = asistenciaClaseAerobica;
    }

    // Método toString para facilitar la visualización de los datos 
    @Override
    public String toString() {
        return "ClaseAerobica{" +
                "idClase=" + idClase +
                ", capacidad=" + capacidad +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", horarioPorDia=" + horarioPorDia +
                '}';
    }
}
