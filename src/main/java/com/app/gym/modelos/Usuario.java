package com.app.gym.modelos;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios") 
public class Usuario {

    // Esta clase representa a un usuario del sistema de gestión de gimnasio. Cada instancia de esta clase corresponde a un usuario específico

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;
    
    private String nombre;

    private String apellido;
    
    // Atributo para almacenar la fecha de nacimiento del usuario. En postgresql, será de tipo DATE.
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    // Atributo para almacenar la fecha de inicio y fin de la membresía del usuario.
    // En postgresql, será de tipo DATE.
    @Column(name = "fecha_inicio_membresia", nullable = false)
    private LocalDate fechaInicioMembresia;

    @Column(name = "fecha_fin_membresia", nullable = false)
    private LocalDate fechaFinMembresia;

    // Indica si la venta está anulada (soft-delete)
    @Column(nullable = false)
    private Boolean anulada = false; 

    // Relación con la entidad Membresia.
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading para optimizar rendimiento
    @JoinColumn(name = "id_membresia", referencedColumnName = "id_membresia", nullable = false)
    private Membresia membresia;

    @OneToOne
    @JoinColumn(name = "id_account")
    private Account account;

    //Relación con la entidad Venta.
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Venta> venta;

    // Relación con la entidad Asistencia General.
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<AsistenciaGeneral> asistenciaGeneral;

    public Usuario() {
    }

    // Getters y Setters
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaInicioMembresia() {
        return fechaInicioMembresia;
    }

    public void setFechaInicioMembresia(LocalDate fechaInicioMembresia) {
        this.fechaInicioMembresia = fechaInicioMembresia;
    }

    public LocalDate getFechaFinMembresia() {
        return fechaFinMembresia;
    }

    public void setFechaFinMembresia(LocalDate fechaFinMembresia) {
        this.fechaFinMembresia = fechaFinMembresia;
    }

    public Membresia getMembresia() {
        return membresia;
    }

    public void setMembresia(Membresia membresia) {
        this.membresia = membresia;
    }

    public List<Venta> getVenta() {
        return venta;
    }

    public void setVenta(List<Venta> venta) {
        this.venta = venta;
    }

    public List<AsistenciaGeneral> getAsistenciaGeneral() {
        return asistenciaGeneral;
    }

    public void setAsistenciaGeneral(List<AsistenciaGeneral> asistenciaGeneral) {
        this.asistenciaGeneral = asistenciaGeneral;
    }

    public Boolean getAnulada() {
        return anulada;
    }

    public void setAnulada(Boolean anulada) {
        this.anulada = anulada;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    // Método toString para facilitar la visualización de los datos
    @Override
    public String toString() {
        return "Usuario{" +
            "idUsuario=" + idUsuario +
            ", nombre='" + nombre + '\'' +
            ", apellido='" + apellido + '\'' +
            ", fechaNacimiento=" + fechaNacimiento +
            ", fechaInicioMembresia=" + fechaInicioMembresia +
            ", fechaFinMembresia=" + fechaFinMembresia +
            ", anulada=" + anulada +
            ", membresia=" + (membresia != null ? membresia.getNombre() : "Sin membresía") + 
            '}';
}
}