package com.app.gym.dtos.membresia;

import java.math.BigDecimal;

public class MembresiaResponseDTO {

    private Integer idMembresia; 

    //Nombre de la membresía, por ejemplo: quincenal, mensual, trimestral, anual, etc.
    private String nombre;

    //Cuál es la duración de la membresía (Ej: 15 días, 1 mes, 3 meses, 1 año...).
    private String duracion;

    //Precio de la membresía.
    private BigDecimal precio;

    private Boolean anulada;

    //Getters y setters

    public Integer getIdMembresia() {
        return idMembresia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDuracion() {
        return duracion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setIdMembresia(Integer idMembresia) {
        this.idMembresia = idMembresia;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Boolean getAnulada() {
        return anulada;
    }

    public void setAnulada(Boolean anulada) {
        this.anulada = anulada;
    }
}
