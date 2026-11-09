package com.app.gym.dtos.horarioPorDia;

public class HorarioPorDiaSimpleDTO {
    
    /*
     * Esta clase es un DTO (Data Transfer Object) que representa un horario por día
     * de manera simplificada, sin las relaciones con otras entidades.
     * Se utiliza para transferir datos entre la capa de servicio y la capa de presentación.
     */

    private Integer idHorario;
    private String dia;

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
}
