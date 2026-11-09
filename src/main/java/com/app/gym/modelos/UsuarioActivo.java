package com.app.gym.modelos;

import java.sql.Date;
import org.hibernate.annotations.Immutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad que mapea la vista SQL 'usuarios_activos'.
 * Representa usuarios con estado de membresía activo o vencido.
 * Esta clase es de solo lectura: Hibernate no realizará operaciones
 * de INSERT, UPDATE ni DELETE sobre la vista.
 */
@Entity
@Immutable
@Table(name = "usuarios_activos")
public class UsuarioActivo {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // Nombre del usuario.
    private String nombre;

    // Apellido del usuario.
    private String apellido;

    // Fecha de inicio de la membresía.
    @Column(name = "fecha_inicio_membresia")
    private Date fechaInicioMembresia;

    // Fecha de fin de la membresía.
    @Column(name = "fecha_fin_membresia")
    private Date fechaFinMembresia;

    // Nombre del tipo de membresía asociada al usuario.
    @Column(name = "tipo_membresia")
    private String tipoMembresia;

    // Duración de la membresía en días o meses, según definición en la DB.
    private String duracion;

    // Indicador de si la membresía está activa. true = vigente, false = vencida.
    @Column(name = "membresia_activa")
    private Boolean membresiaActiva;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Date getFechaInicioMembresia() {
        return fechaInicioMembresia;
    }

    public Date getFechaFinMembresia() {
        return fechaFinMembresia;
    }

    public String getTipoMembresia() {
        return tipoMembresia;
    }

    public String getDuracion() {
        return duracion;
    }

    public Boolean getMembresiaActiva() {
        return membresiaActiva;
    }
}