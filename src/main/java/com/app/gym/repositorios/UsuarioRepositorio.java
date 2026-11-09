package com.app.gym.repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.gym.modelos.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    
    /** Busca un usuario por su ID.
     * @param idUsuario el ID del usuario
     * @return un Optional que contiene el usuario si se encuentra, o vacío si no
     */
    Optional<Usuario> findByIdUsuario(Integer idUsuario);
    
    /** Busca todos los usuarios cuya membresía está por vencer en los próximos 7 días.
     * @return una lista de usuarios con membresía por vencer
     */
    @Query("SELECT u FROM Usuario u WHERE u.fechaFinMembresia BETWEEN :inicio AND :fin")
    List<Usuario> findUsuariosPorVencer(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}

