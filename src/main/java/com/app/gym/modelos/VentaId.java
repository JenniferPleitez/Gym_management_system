package com.app.gym.modelos;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class VentaId implements Serializable{

    /*
     * Clase que representa una clave primaria compuesta para la entidad Venta.
     * Esta clase implementa Serializable para permitir su uso como clave primaria en JPA.
     */
    
    private Integer idVenta; 
    private LocalDate fecha;

    public VentaId() {
    }

    public VentaId(Integer idVenta, LocalDate fecha) {
        this.idVenta = idVenta;
        this.fecha = fecha;
    }

    // Getters y setters
    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Compara dos objetos VentaId para verificar si son iguales.
     *
     * @param o Objeto a comparar con la instancia actual.
     * @return true si ambos objetos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VentaId)) return false;
        VentaId ventaId = (VentaId) o;
        return idVenta.equals(ventaId.idVenta) && fecha.equals(ventaId.fecha);
    }

    /**
     * Genera un código hash para la clave compuesta.
     *
     * @return Código hash basado en los campos idVenta y fecha.
     */
    @Override
    public int hashCode() {
        return Objects.hash(idVenta, fecha);
    }
}
