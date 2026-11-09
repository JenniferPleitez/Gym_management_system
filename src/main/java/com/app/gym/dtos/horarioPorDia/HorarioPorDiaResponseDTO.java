package com.app.gym.dtos.horarioPorDia;

import java.time.LocalTime;

public class HorarioPorDiaResponseDTO {

    private Integer idHorario;

    // Ej: "lunes", "martes", "miércoles"...
    private String dia;

    //Atributos para almacenar la hora de apertura y cierre del gimnasio.
    private LocalTime horaApertura;
    private LocalTime horaCierre;

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
}
