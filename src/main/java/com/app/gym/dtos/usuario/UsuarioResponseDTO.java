package com.app.gym.dtos.usuario;

import java.time.LocalDate;

import com.app.gym.dtos.membresia.MembresiaSimpleDTO;

public class UsuarioResponseDTO {

    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento; 
    private LocalDate fechaInicioMembresia;
    private LocalDate fechaFinMembresia;
    private MembresiaSimpleDTO membresia;
    private Boolean anulada;

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
    public MembresiaSimpleDTO getMembresia() {
        return membresia;
    }
    public void setMembresia(MembresiaSimpleDTO membresia) {
        this.membresia = membresia;
    }
    public Boolean getAnulada() {
        return anulada;
    }
    public void setAnulada(Boolean anulada) {
        this.anulada = anulada;
    }
}
