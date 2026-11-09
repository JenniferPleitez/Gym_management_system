package com.app.gym.dtos.venta;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;

/**
 * DTO usado para actualizar una venta:
 * - cantidad: nueva cantidad de productos
 * - anulada: opcional, marca si la venta está anulada
 */
public class VentaActualizarDto {

    private LocalDate fecha;
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    public Boolean anulada = false;  // Marca la venta como anulada (soft-delete) que por defecto el false
    private Integer usuario; //Relación con usuario y producto
    private Integer producto; // Relación con usuario y producto

    //Getters 
    public LocalDate getFecha() {
        return fecha;
    }

    public Integer getCantidad() {
        return cantidad;
    }
    
    public Boolean getAnulada() {
        return anulada;
    }
    
    public Integer getUsuario() {
        return usuario;
    }

    public Integer getProducto() {
        return producto;
    }
}
