package com.app.gym.servicios;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.horarioPorDia.HorarioPorDiaActualizarDTO;
import com.app.gym.dtos.horarioPorDia.HorarioPorDiaResponseDTO;
import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
import com.app.gym.modelos.HorarioPorDia;
import com.app.gym.repositorios.HorarioPorDiaRepositorio;
import com.app.gym.utils.ListUtils;

@Service
public class HorarioPorDiaServicio {
    private HorarioPorDiaRepositorio horarioPorDiaRepositorio;

    public HorarioPorDiaServicio(HorarioPorDiaRepositorio horarioPorDiaRepositorio) {
        this.horarioPorDiaRepositorio = horarioPorDiaRepositorio;
    }

    // ------------------------------------------------------------------
    // Operaciones de actualización
    // ------------------------------------------------------------------
    
    @Transactional
    public HorarioPorDia actualizarHorarioPorDia(Integer id, HorarioPorDiaActualizarDTO dto) {
        
        HorarioPorDia horarioPorDia = horarioPorDiaRepositorio.findById(id)
                                            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No se ha encontrado un horario con el ID: " + id));

        if(dto.getHoraApertura() != null){
            horarioPorDia.setHoraApertura(dto.getHoraApertura());
        }

        if(dto.getHoraCierre() != null){
            horarioPorDia.setHoraCierre(dto.getHoraCierre());
        }

        return horarioPorDiaRepositorio.save(horarioPorDia);
    }

    @Transactional(readOnly = true)
    public HorarioPorDia obtenerHorarioPorId(Integer id) {
        return horarioPorDiaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No se ha encontrado un horario con el ID: " + id));
    }

    // ------------------------------------------------------------------
    // Métodos de consulta
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<HorarioPorDia> obtenerHorarios() {
        return ListUtils.emptyIfNull(horarioPorDiaRepositorio.findAll());
    }

    public HorarioPorDiaResponseDTO toResponseDTO(HorarioPorDia horarioPorDia){

        HorarioPorDiaResponseDTO dto = new HorarioPorDiaResponseDTO();

        dto.setIdHorario(horarioPorDia.getIdHorario());
        dto.setDia(horarioPorDia.getDia());
        dto.setHoraApertura(horarioPorDia.getHoraApertura());
        dto.setHoraCierre(horarioPorDia.getHoraCierre());
        return dto;
    }

    // Variante para listas
    public List<HorarioPorDiaResponseDTO> toResponseDTO(List<HorarioPorDia> horarios) {
        return horarios.stream().map(this::toResponseDTO).toList();
    }
}
