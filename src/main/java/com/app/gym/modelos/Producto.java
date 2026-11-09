package com.app.gym.modelos;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

    //Almacena todos los productos que se venden en el gimnasio (como camisas, centros, proteína, creatina, galletas, etc...)------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto", nullable = false)
    private Integer idProducto; 

    //Nombre del producto, por ejemplo: camiseta, proteína, etc.
    @Column(length = 50) 
    private String nombre;

    /*
     * Descripción del producto, por ejemplo: camiseta de algodón, proteína de suero, etc.  
     * Nota: Usamos TEXT para permitir descripciones largas, ya que algunos productos pueden tener descripciones extensas.
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    //Precio del producto.
    @Column(precision = 5, scale = 2)
    private BigDecimal precio;
    
    //Cantidad disponible del producto en inventario.
    @Column(name = "cantidad_disponible")
    private Integer cantidadDisponible;

    //Esta relacionada muchos a uno con Venta, un producto puede aparecer en muchas ventas.
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL) 
    private List<Venta> venta;
    
    @Column(nullable = false)
    private boolean anulada = false; // Marca si la asistencia está anulada (soft-delete)

    public Producto() {
    }

    //Getters y setters
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public List<Venta> getVenta() {
        return venta;
    }

    public void setVenta(List<Venta> venta) {
        this.venta = venta;
    }  
    
    public boolean getAnulada() {
        return anulada;
    }

    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }
    
    /*  
     *  Métodos adicionales --------------------------------------------------------------------------------------------
     */
    // Método para mostrar la información del producto
    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", cantidadDisponible=" + cantidadDisponible +
                '}';
    }
}
