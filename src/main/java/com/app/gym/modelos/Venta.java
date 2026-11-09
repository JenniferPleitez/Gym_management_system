package com.app.gym.modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity; 
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@IdClass(VentaId.class) // Entidad con clave primaria compuesta
@Table(name = "ventas")
public class Venta {
    
    /*
     * Clase que representa una venta en el sistema de gestión de gimnasio.
     * Utiliza una clave primaria compuesta formada por el identificador de la venta y la fecha de la venta.
     */
    @Id  
    @Column(name = "id_venta", nullable = false)
    private Integer idVenta; 

    @Id
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    // Cantidad de productos vendidos en esta venta.
    @Column(nullable = false)
    private Integer cantidad;

    // Cantidad de productos vendidos en esta venta.
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal total;

    // Indica si la venta está anulada (soft-delete)
    @Column(nullable = false)
    public Boolean anulada = false; 

    //Relación con usuario y producto:
    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario; 
    
    @ManyToOne
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto", nullable = false)
    private Producto producto; //ponemos el nombre del linker

    // Getters y Setters
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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Boolean getAnulada() {
        return anulada;
    }

    public void setAnulada(Boolean anulada) {
        this.anulada = anulada;
    }

    // Método toString para facilitar la visualización de los datos de Venta.
    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", fecha=" + fecha +
                ", cantidad=" + cantidad +
                ", total=" + total +
                ", anulada=" + anulada +
                ", usuario=" + usuario +
                ", producto=" + producto +
                '}';
    }
}
