package com.app.gym.modelos;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "membresias")
public class Membresia {

    //Esta tabla almacena los planes disponibles para pagar en el gimnasio y sus precios ------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membresia", nullable = false)
    private Integer idMembresia; 

    //Nombre de la membresía, por ejemplo: quincenal, mensual, trimestral, anual, etc.
    @Column(length = 30)
    private String nombre;

    //Cuál es la duración de la membresía (Ej: 15 días, 1 mes, 3 meses, 1 año...).
    @Column(length = 10, columnDefinition = "interval")
    @Type(PostgreSQLIntervalType.class) // Usa la anotación @Type de Hibernate
    private Duration duracion;

    //Precio de la membresía.
    @Column(precision = 5, scale = 2) // Hasta 5 dígitos en total, con 2 decimales
    // Ejemplo: 999.99
    private BigDecimal precio;

    @Column(nullable = false)
    private boolean anulada = false; // Marca si la asistencia está anulada (soft-delete)

    //Esta relaciona uno a uno con Usuarios, donde cada usuario puede tener una membresía.
    @OneToMany(mappedBy = "membresia", fetch = FetchType.LAZY) // <<-- Many usuarios
    private List<Usuario> usuarios;

    public Membresia() {
    }

    //Getters y setters  ----------------------------------------------------------------------------------------------------
    public Integer getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(Integer idMembresia) {
        this.idMembresia = idMembresia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Duration getDuracion() {
        return duracion;
    }

    public void setDuracion(Duration duracion) {
        this.duracion = duracion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
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
    // Método para mostrar la información de la membresía
    @Override
    public String toString() {
        return "Membresias{" +
                "idMembresia=" + idMembresia +
                ", nombre='" + nombre + '\'' +
                ", duracion='" + duracion + '\'' +
                ", precio=" + precio +
                ", anulada=" + anulada +
                '}';
    }
}
