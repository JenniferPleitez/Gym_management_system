package com.app.gym.dtos.producto;

import java.math.BigDecimal;

public class ProductoResponseDTO {

    // Clase DTO para imprimir los datos del producto

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer cantidadDisponible;
    private boolean anulada;

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }
    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }
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
