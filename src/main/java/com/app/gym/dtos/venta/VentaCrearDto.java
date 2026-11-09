package com.app.gym.dtos.venta;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class VentaCrearDto {
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    /** Opcional: si no viene se usará la fecha actual. */
    private LocalDate fecha;

    @NotNull(message = "Debe indicar el usuario")
    private Integer idUsuario;

    @NotNull(message = "Debe indicar el producto")
    private Integer idProducto;

    public Integer getCantidad() {
        return cantidad;
    }

    // Getter y Setter
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    
}
