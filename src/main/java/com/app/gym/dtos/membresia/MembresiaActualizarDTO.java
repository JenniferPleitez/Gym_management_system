package com.app.gym.dtos.membresia;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;

public class MembresiaActualizarDTO {

    private String nombre;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private String unidad; // "dias", "meses", "años"

    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private BigDecimal precio;

    private Boolean anulada;

    // Getters
    public String getNombre() {
        return nombre;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public String getUnidad() {
        return unidad;
    }
    public Boolean getAnulada() {
        return anulada;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public void setAnulada(Boolean anulada) {
        this.anulada = anulada;
    }
}
