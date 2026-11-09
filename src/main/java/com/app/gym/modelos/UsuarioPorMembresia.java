package com.app.gym.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Immutable // Indica que esta entidad es de solo lectura y no se debe modificar
@Table(name = "usuarios_por_membresia")
public class UsuarioPorMembresia {

    /**
    * Entidad de solo lectura que representa la vista SQL 'usuarios_por_membresia'.
    * Se utiliza para mostrar un resumen por tipo de membresía con cantidad de usuarios
    * y el monto total generado.
    */
    
    @Id
    @Column(name = "membresia")
    private String membresia;

    // Precio de la membresía.
    private Double precio;

    // Cantidad de usuarios asociados a esta membresía.
    @Column(name = "cantidad_usuarios")
    private Integer cantidadUsuarios;

    // Monto total generado por esta membresía.
    @Column(name = "monto_total_generado")
    private Double montoTotalGenerado;

    public String getMembresia() {
        return membresia;
    }

    public Double getPrecio() {
        return precio;
    }

    public Integer getCantidadUsuarios() {
        return cantidadUsuarios;
    }

    public Double getMontoTotalGenerado() {
        return montoTotalGenerado;
    }
}
