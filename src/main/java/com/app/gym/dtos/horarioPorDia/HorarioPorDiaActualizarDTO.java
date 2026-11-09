package com.app.gym.dtos.horarioPorDia;

import java.time.LocalTime;

public class HorarioPorDiaActualizarDTO {

    //Atributos para almacenar la hora de apertura y cierre del gimnasio.
    private LocalTime horaApertura;

    private LocalTime horaCierre;

    // Getters
    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }
}
