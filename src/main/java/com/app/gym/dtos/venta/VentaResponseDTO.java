package com.app.gym.dtos.venta;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.app.gym.dtos.producto.ProductoSimpleDTO;
import com.app.gym.dtos.usuario.UsuarioSimpleDTO;

public class VentaResponseDTO {
    
    private Integer idVenta;
    private LocalDate fecha;
    private Integer cantidad;
    private BigDecimal total;
    private Boolean anulada;
    private UsuarioSimpleDTO usuario;
    private ProductoSimpleDTO producto;

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
    public Boolean getAnulada() {
        return anulada;
    }
    public void setAnulada(Boolean anulada) {
        this.anulada = anulada;
    }
    public UsuarioSimpleDTO getUsuario() {
        return usuario;
    }
    public void setUsuario(UsuarioSimpleDTO usuario) {
        this.usuario = usuario;
    }
    public ProductoSimpleDTO getProducto() {
        return producto;
    }
    public void setProducto(ProductoSimpleDTO producto) {
        this.producto = producto;
    }

}
