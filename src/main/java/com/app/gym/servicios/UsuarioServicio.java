package com.app.gym.servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.membresia.MembresiaSimpleDTO;
import com.app.gym.dtos.usuario.UsuarioActualizarDTO;
import com.app.gym.dtos.usuario.UsuarioRequestDTO;
import com.app.gym.dtos.usuario.UsuarioResponseDTO;
import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
import com.app.gym.mappers.UsuarioMapper;
import com.app.gym.modelos.Membresia;
import com.app.gym.modelos.Usuario;
import com.app.gym.repositorios.MembresiaRepositorio;
import com.app.gym.repositorios.UsuarioRepositorio;
import com.app.gym.utils.ListUtils;
import com.app.gym.utils.validadores.FechaValidador;

import jakarta.persistence.EntityManager;

@Service
public class UsuarioServicio {
    
    //TODO: PONER PRIVATE Y SEPARAR EN APP SERVICE
    UsuarioRepositorio usuarioRepositorio;
    MembresiaRepositorio membresiaRepositorio;
    UsuarioMapper usuarioMapper;
    EntityManager entityManager;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio, MembresiaRepositorio membresiaRepositorio, EntityManager entityManager, UsuarioMapper usuarioMapper) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.membresiaRepositorio = membresiaRepositorio;
        this.entityManager = entityManager;
        this.usuarioMapper = usuarioMapper;
    }
    // ------------------------------------------------------------------
    // Operaciones de creación
    // ------------------------------------------------------------------
    
    /**
     * Registra un nuevo usuario en el sistema.
     * @param dto El DTO que contiene los datos del usuario a registrar.    
     * @return El usuario registrado.
     */
    @Transactional
    public Usuario registrarUsuario(UsuarioRequestDTO dto){

        //validar que la membresía exista
        Membresia membresia = membresiaRepositorio.findById(dto.getIdMembresia())
                                                .orElseThrow(() -> new RuntimeException("Membresía no encontrada"));
        
        FechaValidador.validarFechaNacimiento(dto.getFechaNacimiento());

        LocalDate fecha = dto.getFechaInicioMembresia() != null ? dto.getFechaInicioMembresia() : LocalDate.now();
        
        FechaValidador.validarFecha(fecha);
        FechaValidador.validarFechaNacimiento(dto.getFechaNacimiento());

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre().trim().toLowerCase());
        usuario.setApellido(dto.getApellido().trim().toLowerCase());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setFechaInicioMembresia(fecha);
        usuario.setMembresia(membresia);

        Usuario usuarioActualizado = usuarioRepositorio.saveAndFlush(usuario);

        entityManager.refresh(usuarioActualizado);

        return usuarioActualizado;
    }

    // ------------------------------------------------------------------
    // Operaciones de actualización
    // ------------------------------------------------------------------
    @Transactional
    public Usuario actualizarUsuario(Integer id, UsuarioActualizarDTO dto){
        Usuario usuario = usuarioRepositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario no encontrado"));

        usuarioMapper.updateFromDto(dto, usuario, entityManager);

        Usuario usuarioActualizado = usuarioRepositorio.saveAndFlush(usuario);
        entityManager.refresh(usuarioActualizado);

        return usuarioActualizado;
    }

    // ------------------------------------------------------------------
    // Métodos de consulta
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Integer id){
        return usuarioRepositorio.findByIdUsuario(id)
                                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Usuario> usuariosPorVencerEnUnaSemana(){

        // Definir el rango de fechas para buscar usuarios con membresía por vencer
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(7);
        
        return ListUtils.emptyIfNull(usuarioRepositorio.findUsuariosPorVencer(inicio, fin));
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario){
        MembresiaSimpleDTO m = new MembresiaSimpleDTO();
        m.setIdMembresia(usuario.getMembresia().getIdMembresia());
        m.setNombre(usuario.getMembresia().getNombre());

        UsuarioResponseDTO u = new UsuarioResponseDTO();
        u.setIdUsuario(usuario.getIdUsuario());
        u.setNombre(usuario.getNombre());
        u.setApellido(usuario.getApellido());
        u.setFechaNacimiento(usuario.getFechaNacimiento());
        u.setFechaInicioMembresia(usuario.getFechaInicioMembresia());
        u.setFechaFinMembresia(usuario.getFechaFinMembresia());
        u.setAnulada(usuario.getAnulada());
        u.setMembresia(m);

        return u;
    }

    public List<UsuarioResponseDTO> toResponseDTO(List<Usuario> usuarios) {
        return usuarios.stream().map(this::toResponseDTO).toList();
    }
}
