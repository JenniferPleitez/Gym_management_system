package com.app.gym.dtos.producto;

import java.math.BigDecimal;

public class ProductoSimpleDTO {
    
    /*
     * Esta clase es un DTO (Data Transfer Object) que representa un producto de manera
     * simplificada, sin las relaciones con otras entidades.
     * Se utiliza para transferir datos entre la capa de servicio y la capa de presentación.
     */

    private Integer idProducto;
    private String nombre;
    private BigDecimal precio;

    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) { 
        this.precio = precio;
    }   
}
