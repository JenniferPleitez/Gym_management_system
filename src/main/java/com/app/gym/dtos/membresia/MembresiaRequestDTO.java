package com.app.gym.dtos.membresia;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class MembresiaRequestDTO {

    @NotBlank(message = "El nombre no puede ser nulo")
    private String nombre;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    @NotBlank(message = "La unidad no puede ser nula")
    private String unidad; // "dias", "meses", "años"

    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private BigDecimal precio;

    // Getters
    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
}
