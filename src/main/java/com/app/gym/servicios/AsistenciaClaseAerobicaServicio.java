package com.app.gym.servicios;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.asistenciaClaseAerobica.AsistenciaClaseAerobicaActualizarDTO;
import com.app.gym.dtos.asistenciaClaseAerobica.AsistenciaClaseAerobicaRequestDTO;
import com.app.gym.dtos.asistenciaClaseAerobica.AsistenciaClaseAerobicaResponseDTO;
import com.app.gym.dtos.asistenciaClaseAerobica.ClaseAerobicaSimpleDTO;
import com.app.gym.dtos.horarioPorDia.HorarioPorDiaSimpleDTO;
import com.app.gym.dtos.usuario.UsuarioSimpleDTO;
import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
import com.app.gym.excepciones.UsuarioSinMembresiaExepcion;
import com.app.gym.modelos.AsistenciaClaseAerobica;
import com.app.gym.modelos.ClaseAerobica;
import com.app.gym.modelos.Usuario;
import com.app.gym.repositorios.AsistenciaClaseAerobicaRepositorio;
import com.app.gym.repositorios.ClaseAerobicaRepositorio;
import com.app.gym.repositorios.UsuarioRepositorio;
import com.app.gym.utils.ListUtils;
import com.app.gym.utils.validadores.FechaValidador;

@Service
public class AsistenciaClaseAerobicaServicio {
    
    AsistenciaClaseAerobicaRepositorio asistenciaClaseAerobicaRepositorio;
    ClaseAerobicaRepositorio claseAerobicaRepositorio;
    UsuarioRepositorio usuarioRepositorio;

    public AsistenciaClaseAerobicaServicio(AsistenciaClaseAerobicaRepositorio asistenciaClaseAerobicaRepositorio,
                                            ClaseAerobicaRepositorio claseAerobicaRepositorio, 
                                            UsuarioRepositorio usuarioRepositorio) {
        this.asistenciaClaseAerobicaRepositorio = asistenciaClaseAerobicaRepositorio;
        this.claseAerobicaRepositorio = claseAerobicaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    // ------------------------------------------------------------------
    // Operaciones de creación
    // ------------------------------------------------------------------
    
    // Este método permite registrar la asistencia de un usuario a una clase aeróbica en una fecha específica.
    @Transactional
    public AsistenciaClaseAerobica registrarAsistenciaClaseAerobica(AsistenciaClaseAerobicaRequestDTO dto){

        // Validar que la clase aerobica y el usuario existan
        ClaseAerobica claseAerobica = claseAerobicaRepositorio.findById(dto.getIdClase())
                                                                        .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Clase Aerobica no encontrada con ID: " + dto.getIdClase()));

        Usuario usuario = usuarioRepositorio.findById(dto.getIdUsuario())
                                            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario no encontrado con ID: " + dto.getIdUsuario()));

        //Validar que el usuario tenga una membresía activa. Si la fecha de fin de la membresía es nula o anterior a la fecha actual, se lanza una excepción.
        FechaValidador.validarFechaFinMembresia(usuario.getFechaFinMembresia());

        // Si la fecha no se proporciona, usar la fecha actual:
        LocalDate fecha = dto.getFecha() != null ? dto.getFecha() : LocalDate.now();

        // Validar la fecha proporcionada
        FechaValidador.validarFecha(fecha);

        // Crear la asistencia
        AsistenciaClaseAerobica asistencia = new AsistenciaClaseAerobica();
        asistencia.setFecha(fecha);
        asistencia.setClaseAerobica(claseAerobica);
        asistencia.setUsuario(usuario);

        return asistenciaClaseAerobicaRepositorio.save(asistencia);
    }

    // ------------------------------------------------------------------
    // Operaciones de actualización
    // ------------------------------------------------------------------
    
    // Este método permite modificar completamente o parcialmente: la fecha, el estado de anulación, la clase asociada y el usuario de una asistencia de clase aeróbica.
    @Transactional
    public AsistenciaClaseAerobica actualizarAsistenciaClaseAerobica (Integer idAsistencia, AsistenciaClaseAerobicaActualizarDTO dto){

        AsistenciaClaseAerobica asistencia = asistenciaClaseAerobicaRepositorio.findById(idAsistencia).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Asistencia no encontrada con ID: " + idAsistencia));

            if(dto.getFecha() != null){

                // Validar la fecha proporcionada
                FechaValidador.validarFecha(dto.getFecha()); 
                asistencia.setFecha(dto.getFecha());
            }
            
            // Actualizar estado de anulación (soft-delete) si se proporciona
            if(dto.getAnulada() != null){
                asistencia.setAnulada(dto.getAnulada());
            }

            if(dto.getIdClase() != null){
                ClaseAerobica clase = claseAerobicaRepositorio.findById(dto.getIdClase())
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Clase Aerobica no encontrada con el ID: " + dto.getIdClase()));	
                
                asistencia.setClaseAerobica(clase);
            }

            if(dto.getIdUsuario() != null){
                Usuario usuario = usuarioRepositorio.findById(dto.getIdUsuario()).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario no encontrada con el ID: " + dto.getIdUsuario()));
            
                if(usuario.getFechaFinMembresia() == null || usuario.getFechaFinMembresia().isBefore(LocalDate.now())){
                    throw new UsuarioSinMembresiaExepcion("El usuario no tiene una membresía activa");
                }
                
                asistencia.setUsuario(usuario);
            }

            return asistenciaClaseAerobicaRepositorio.save(asistencia);
    }

    // ------------------------------------------------------------------
    // Operaciones de consulta
    // ------------------------------------------------------------------

    // Obtiene una asistencia de clase aeróbica por su ID.
    @Transactional(readOnly = true)
    public AsistenciaClaseAerobica obtenerAsistenciaPorId(Integer idAsistencia){
        return asistenciaClaseAerobicaRepositorio.findByidAsistenciaClaseAerobica(idAsistencia)
                                                    .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Asistencia no encontrada con ID: " + idAsistencia));
    }

    // Obtiene todas las asistencias de clase aeróbica realizadas en una fecha específica.
    @Transactional(readOnly = true)
    public List<AsistenciaClaseAerobica> obtenerAsistenciasPorFecha(LocalDate fecha){
        FechaValidador.validarFecha(fecha); // Validar que la fecha no sea futura y esté dentro de un rango razonable
        return ListUtils.emptyIfNull(asistenciaClaseAerobicaRepositorio.findByFecha(fecha)); // Si no se encuentran asistencias, devolver una lista vacía
    }

    // Obtiene todas las asistencias de clase aeróbica realizadas en un mes y año específicos.
    @Transactional(readOnly = true)
    public List<AsistenciaClaseAerobica> obtenerAsistenciasPorMes(int anio, int mes) {

        FechaValidador.validarYearMonth(anio, mes); // Validar que el año y mes sean válidos
        YearMonth ym = YearMonth.of(anio, mes);
        return ListUtils.emptyIfNull(asistenciaClaseAerobicaRepositorio.findByRangoFechas(ym.atDay(1), ym.atEndOfMonth())); // Si no se encuentran asistencias, devolver una lista vacía
    }

    // Obtiene todas las asistencias de clase aeróbica realizadas en un rango de fechas específico.
    @Transactional(readOnly = true)
    public List<AsistenciaClaseAerobica> obtenerAsistenciasPorRangoFechas(LocalDate inicio, LocalDate fin){

        FechaValidador.validarRangoFechas(inicio, fin); // Validar que el rango de fechas sea correcto
        return ListUtils.emptyIfNull(asistenciaClaseAerobicaRepositorio.findByRangoFechas(inicio, fin)); // Si no se encuentran asistencias, devolver una lista vacía
    }

    // Convierte una entidad AsistenciaClaseAerobica a un DTO de respuesta.
    public AsistenciaClaseAerobicaResponseDTO toResponseDTO(AsistenciaClaseAerobica asistenciaClaseAerobica){

        HorarioPorDiaSimpleDTO h = new HorarioPorDiaSimpleDTO();
        h.setIdHorario(asistenciaClaseAerobica.getClaseAerobica().getHorarioPorDia().getIdHorario());
        h.setDia(asistenciaClaseAerobica.getClaseAerobica().getHorarioPorDia().getDia());

        ClaseAerobicaSimpleDTO c = new ClaseAerobicaSimpleDTO();
        c.setIdClaseAerobica(asistenciaClaseAerobica.getClaseAerobica().getIdClase());
        c.setHoraInicio(asistenciaClaseAerobica.getClaseAerobica().getHoraInicio());
        c.setHoraFin(asistenciaClaseAerobica.getClaseAerobica().getHoraFin());
        c.setHorarioPorDia(h);
        
        UsuarioSimpleDTO u = new UsuarioSimpleDTO();
        u.setIdUsuario(asistenciaClaseAerobica.getUsuario().getIdUsuario());
        u.setNombre(asistenciaClaseAerobica.getUsuario().getNombre());
        u.setApellido(asistenciaClaseAerobica.getUsuario().getApellido());
        
        AsistenciaClaseAerobicaResponseDTO a = new AsistenciaClaseAerobicaResponseDTO();
        a.setIdAsistenciaAerobica(asistenciaClaseAerobica.getIdAsistenciaClaseAerobica());
        a.setFecha(asistenciaClaseAerobica.getFecha());
        a.setAnulada(asistenciaClaseAerobica.getAnulada());
        a.setIdClase(c);
        a.setUsuario(u);

        return a;
    } 

    // Convierte una lista de entidades AsistenciaClaseAerobica a una lista de DTOs de respuesta.
    public List<AsistenciaClaseAerobicaResponseDTO> toResponseDTO(List<AsistenciaClaseAerobica> asistencias) {
        return asistencias.stream().map(this::toResponseDTO).toList();
    }
}
