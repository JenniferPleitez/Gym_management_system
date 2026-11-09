package com.app.gym.dtos.producto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;

public class ProductoActualizarDTO {

    // Clase DTO para actualizar los datos del producto

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    private Integer cantidadDisponible;
    private boolean anulada; // Marca el producto como anulado (soft-delete) que por defecto es false

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
    public boolean getAnulada() {
        return anulada;
    }
}
