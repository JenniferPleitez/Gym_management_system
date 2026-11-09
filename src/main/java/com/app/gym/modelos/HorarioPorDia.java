package com.app.gym.modelos;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "horarios_por_dia")
public class HorarioPorDia {
    
    /*
     * Esta clase representa los horarios de apertura y cierre del gimnasio por día.
     * Cada instancia de esta clase corresponde a un día específico y contiene la hora de apertura y cierre.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario", nullable = false)
    private Integer idHorario;

    // Ej: "lunes", "martes", "miércoles"...
    private String dia;

    //Atributos para almacenar la hora de apertura y cierre del gimnasio.
    @Column(name = "hora_apertura")
    private LocalTime horaApertura;

    @Column(name = "hora_cierre")
    private LocalTime horaCierre;

    //Relación con la entidad AsistenciaGeneral.
    @OneToMany(mappedBy = "horarioPorDia", cascade = CascadeType.ALL)
    private List<AsistenciaGeneral> asistenciaGeneral;

    //Relación con la entidad ClasesAerobicas.
    @OneToMany(mappedBy = "horarioPorDia", cascade = CascadeType.ALL)
    private List<ClaseAerobica> claseAerobica;

    // Getters y Setters
    public Integer getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Integer idHorario) {
        this.idHorario = idHorario;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(LocalTime horaApertura) {
        this.horaApertura = horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }

    public List<AsistenciaGeneral> getAsistenciaGeneral() {
        return asistenciaGeneral;
    }

    public void setAsistenciaGeneral(List<AsistenciaGeneral> asistenciaGeneral) {
        this.asistenciaGeneral = asistenciaGeneral;
    }

    public List<ClaseAerobica> getClaseAerobica() {
        return claseAerobica;
    }

    public void setClaseAerobica(List<ClaseAerobica> claseAerobica) {
        this.claseAerobica = claseAerobica;
    }

    // Método toString para facilitar la visualización de los datos 
    @Override
    public String toString() {
        return "HorarioPorDia{" +
                "idHorario=" + idHorario +
                ", dia='" + dia + '\'' +
                ", horaApertura=" + horaApertura +
                ", horaCierre=" + horaCierre +
                '}';
    }
    
}
