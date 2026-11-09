package com.app.gym.servicios;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.app.gym.dtos.asistenciaGeneral.AsistenciaGeneralActualizarDTO;
import com.app.gym.dtos.asistenciaGeneral.AsistenciaGeneralResponseDTO;
import com.app.gym.dtos.asistenciaGeneral.AsistenciaRequestDTO;
import com.app.gym.dtos.horarioPorDia.HorarioPorDiaSimpleDTO;
import com.app.gym.dtos.usuario.UsuarioSimpleDTO;
import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
import com.app.gym.modelos.AsistenciaGeneral;
import com.app.gym.modelos.HorarioPorDia;
import com.app.gym.modelos.Usuario;
import com.app.gym.repositorios.AsistenciaGeneralRepositorio;
import com.app.gym.repositorios.HorarioPorDiaRepositorio;
import com.app.gym.repositorios.UsuarioRepositorio;
import com.app.gym.utils.ListUtils;
import com.app.gym.utils.validadores.FechaValidador;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AsistenciaGeneralServicio {
    
    private final UsuarioRepositorio usuarioRepositorio;
    private final HorarioPorDiaRepositorio horarioRepositorio;
    private final AsistenciaGeneralRepositorio asistenciaGeneralRepositorio;

    public AsistenciaGeneralServicio(HorarioPorDiaRepositorio horarioRepositorio, AsistenciaGeneralRepositorio asistenciaGeneralRepositorio,
                                    UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.horarioRepositorio = horarioRepositorio;
        this.asistenciaGeneralRepositorio = asistenciaGeneralRepositorio;
    }

    // ------------------------------------------------------------------
    // Operaciones de creación
    // ------------------------------------------------------------------
    
    // Este método permite registrar la asistencia de un usuario a un horario en una fecha específica.
    @Transactional
    public AsistenciaGeneral registrarAsistenciaGeneral(AsistenciaRequestDTO dto){

        // Validar que el usuario y el horario existan
        Usuario usuario = usuarioRepositorio.findById(dto.getIdUsuario())
											.orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario no encontrado con ID: " + dto.getIdUsuario()));

        HorarioPorDia horario = horarioRepositorio.findById(dto.getIdHorario())
                                                    .orElseThrow(() -> new RecursoNoEncontradoExcepcion ("Horario no encontrado con ID: " + dto.getIdHorario()));
        
        // Validar que el usuario tenga una membresía activa. Si la fecha de fin de la membresía es nula o anterior a la fecha actual, se lanza una excepción.
        FechaValidador.validarFechaFinMembresia(usuario.getFechaFinMembresia());

        // Si la fecha no se proporciona, usar la fecha actual:
        LocalDate fecha = dto.getFecha() != null ? dto.getFecha() : LocalDate.now(); 

        // Validar la fecha proporcionada
        FechaValidador.validarFecha(fecha); // Validar la fecha proporcionada

        // Si la hora de entrada no se proporciona, usar la hora actual:
        LocalTime horaEntrada = dto.getHoraEntrada() != null ? dto.getHoraEntrada() : LocalTime.now();

        // 1. Calcular rango del mes (partición)
        YearMonth yearMonth = YearMonth.from(fecha);
        LocalDate inicio = yearMonth.atDay(1);
        LocalDate fin = yearMonth.atEndOfMonth();

        // 2. Obtener el máximo ID en ese mes
        /*  
         * Obtener el máximo ID de asistencia en el mes actual (el cual lo calcula con la fecha "inicio" y "fin" dadas al metodo).
         * Esto permite que las asistencias se registren de manera secuencial por mes.
         * Si no hay asistencias, se iniciará con ID 1. 
         */
        Integer maxId = asistenciaGeneralRepositorio.findMaxIdAsistenciabyMonth(inicio, fin);
        int nuevoId = (maxId != null) ? maxId + 1 : 1;

        AsistenciaGeneral asistencia = new AsistenciaGeneral();
        asistencia.setIdAsistencia(nuevoId);
        asistencia.setFecha(fecha);
        asistencia.setHoraEntrada(horaEntrada);
        asistencia.setHorarioPorDia(horario);
        asistencia.setUsuario(usuario);

        return asistenciaGeneralRepositorio.save(asistencia); 
    }

    // ------------------------------------------------------------------
    // Operaciones de actualización
    // ------------------------------------------------------------------

    // Este método permite modificar completamente o parcialmente: la fecha, el estado de anulación, la hora de entrada, el horario asociado y el usuario de una asistencia general.
    @Transactional
	public AsistenciaGeneral actualizarAsistenciaGeneral(Integer idAsistencia, LocalDate fecha, AsistenciaGeneralActualizarDTO dto){

        AsistenciaGeneral asistencia = asistenciaGeneralRepositorio.findByIdAsistenciaAndFecha(idAsistencia, fecha)
                                                                    .orElseThrow(() -> new RecursoNoEncontradoExcepcion
                                                                    ("Asistencia no encontrada con ID: " + idAsistencia + " y fecha: " + fecha));

        if(dto.getFecha() != null && !dto.getFecha().equals(fecha)) {
        throw new IllegalStateException("No se puede modificar la fecha de una asistencia");
        }   

        if(dto.getHoraEntrada() != null){
            asistencia.setHoraEntrada(dto.getHoraEntrada()); // Actualizar la hora de entrada
        }

        // Si el DTO indica que la asistencia está anulada, se actualiza el estado de la asistencia
        if(dto.getAnulada() != null){
            asistencia.setAnulada(dto.getAnulada());
        }

        if(dto.getHorarioPorDia() != null){
            HorarioPorDia horario = horarioRepositorio.findById(dto.getHorarioPorDia())
                                                        .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Horario no encontrado con ID: " + dto.getHorarioPorDia()));
            asistencia.setHorarioPorDia(horario); // Actualizar el horario de la asistencia
        }

        if(dto.getUsuario() != null){
            Usuario usuario = usuarioRepositorio.findById(dto.getUsuario())
                                                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Usuario no encontrado con ID: " + dto.getUsuario()));
            asistencia.setUsuario(usuario); // Actualizar el usuario de la asistencia
        }

        // Retornar la asistencia.
        return asistenciaGeneralRepositorio.save(asistencia);
    }

    // ------------------------------------------------------------------
    // Operaciones de consulta
    // ------------------------------------------------------------------

    // Busca una asistencia por su ID y fecha.
    @Transactional(readOnly = true)
    public AsistenciaGeneral buscarPorIdFecha (Integer idAsistencia, LocalDate fecha){
        return asistenciaGeneralRepositorio.findByIdAsistenciaAndFecha(idAsistencia, fecha)
                                            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No se ha encontrado una asistencia con el id '" + idAsistencia + "' en la fecha: '" + fecha +"'."));
    }

    // Busca todas las asistencias realizadas en una fecha específica.
    @Transactional(readOnly = true)
    public List<AsistenciaGeneral> buscarPorFecha(LocalDate fecha){

        FechaValidador.validarFecha(fecha); // Validar que la fecha esté dentro del rango permitido
        return ListUtils.emptyIfNull(asistenciaGeneralRepositorio.findByFecha(fecha));
    }

    // Busca todas las asistencias que hay en un mes y año específicos.
    @Transactional(readOnly = true)
    public List<AsistenciaGeneral> buscarPorMes(int year, int month){
        FechaValidador.validarYearMonth(year, month); // Validar que el año y mes estén dentro del rango permitido
        YearMonth ym = YearMonth.of(year, month);
        return ListUtils.emptyIfNull(asistenciaGeneralRepositorio.findByRangoFechas(ym.atDay(1), ym.atEndOfMonth()));
    }

    // Busca todas las asistencias que hay en un rango de fechas específico.
    @Transactional(readOnly = true)
    public List<AsistenciaGeneral> buscarPorRangoFechas(LocalDate inicio, LocalDate fin){
        FechaValidador.validarRangoFechas(inicio, fin); // Validar que el rango de fechas sea correcto
        return ListUtils.emptyIfNull(asistenciaGeneralRepositorio.findByRangoFechas(inicio, fin));
    }

    // Convierte una entidad AsistenciaGeneral a un DTO de respuesta.
    public AsistenciaGeneralResponseDTO toResponseDTO (AsistenciaGeneral asistenciaGeneral){

        HorarioPorDiaSimpleDTO h = new HorarioPorDiaSimpleDTO();
        h.setIdHorario(asistenciaGeneral.getHorarioPorDia().getIdHorario());
        h.setDia(asistenciaGeneral.getHorarioPorDia().getDia());

        UsuarioSimpleDTO u = new UsuarioSimpleDTO();
        u.setIdUsuario(asistenciaGeneral.getUsuario().getIdUsuario());
        u.setNombre(asistenciaGeneral.getUsuario().getNombre());
        u.setApellido(asistenciaGeneral.getUsuario().getApellido());

        AsistenciaGeneralResponseDTO r = new AsistenciaGeneralResponseDTO();
        r.setIdAsistencia(asistenciaGeneral.getIdAsistencia());
        r.setFecha(asistenciaGeneral.getFecha());
        r.setHoraEntrada(asistenciaGeneral.getHoraEntrada());
        r.setAnulada(asistenciaGeneral.getAnulada());
        r.setUsuario(u);
        r.setHorario(h);

        return r; 
    }

    public List<AsistenciaGeneralResponseDTO> toResponseDTO(List<AsistenciaGeneral> asistencias){
        return asistencias.stream()
                    .map(this::toResponseDTO) 
                    .toList(); 
    }
}
