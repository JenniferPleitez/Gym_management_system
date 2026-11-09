package com.app.gym.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.gym.dtos.horarioPorDia.HorarioPorDiaActualizarDTO;
import com.app.gym.servicios.HorarioPorDiaServicio;

@RestController
@RequestMapping("/api/horarios-por-dia")
public class HorariosPorDiaControlador {
    
    private HorarioPorDiaServicio horarioPorDiaServicio;

    public HorariosPorDiaControlador(HorarioPorDiaServicio horarioPorDiaServicio) {
        this.horarioPorDiaServicio = horarioPorDiaServicio;
    }

    /*------------------------------------------------------------------------------------------------------------
     * 1. Actualiza un horario por día (horaApertura - horaCierre). /api/horarios-por-dia/{id}
     *-----------------------------------------------------------------------------------------------------------*/

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> actualizarHorarioPorDia(@PathVariable Integer id, @RequestBody HorarioPorDiaActualizarDTO dto) {
        return ResponseEntity.ok(horarioPorDiaServicio.toResponseDTO(horarioPorDiaServicio.actualizarHorarioPorDia(id, dto)));
    }

    /*----------------------------------------------------------------------------
     * 2. Buscar Horario Por Dia por su ID.      /api/horarios-por-dia/{id}  
     *----------------------------------------------------------------------------*/

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> obtenerHorarioPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(horarioPorDiaServicio.toResponseDTO(horarioPorDiaServicio.obtenerHorarioPorId(id)));
    }

    /*----------------------------------------------------------------------------
     * 3. Listar todos los Horarios Por Dia.      /api/horarios-por-dia  
     *----------------------------------------------------------------------------*/
    @GetMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> obtenerHorarios() {
        return ResponseEntity.ok(horarioPorDiaServicio.toResponseDTO((horarioPorDiaServicio.obtenerHorarios())));
    }
}
