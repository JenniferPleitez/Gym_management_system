package com.app.gym.dtos.usuario;

public class UsuarioSimpleDTO {
    
    /*
     * Esta clase es un DTO (Data Transfer Object) que representa un usuario de manera
     * simplificada, sin las relaciones con otras entidades.
     * Se utiliza para transferir datos entre la capa de servicio y la capa de presentación.
     */

    private Integer idUsuario;
    private String nombre;
    private String apellido;

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
}
