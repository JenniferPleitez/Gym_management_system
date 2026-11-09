package com.app.gym.dtos.producto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ProductoRequestDTO {

    //Clase DTO para la creación de productos.


    @NotNull(message = "El nombre no puede ser nulo")
    private String nombre;
    @NotNull(message = "La descripción no puede ser nula")
    private String descripcion;
    @NotNull(message = "El precio no puede ser nulo")
    private BigDecimal precio;
    @NotNull(message = "La cantidad disponible no puede ser nula")
    @Min(value = 1, message = "La cantidad disponible debe ser al menos 1")
    private Integer cantidadDisponible;

    //Getters y setters
    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }
}
