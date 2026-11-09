package com.app.gym.controladores;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.gym.dtos.asistenciaClaseAerobica.AsistenciaClaseAerobicaActualizarDTO;
import com.app.gym.dtos.asistenciaClaseAerobica.AsistenciaClaseAerobicaRequestDTO;
import com.app.gym.modelos.AsistenciaClaseAerobica;
import com.app.gym.servicios.AsistenciaClaseAerobicaServicio;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/asistencia-clase-aerobica")
public class AsistenciaClaseAerobicaControlador {
    
    private final AsistenciaClaseAerobicaServicio asistenciaClaseAerobicaServicio;

    public AsistenciaClaseAerobicaControlador(AsistenciaClaseAerobicaServicio asistenciaClaseAerobicaServicio) {
        this.asistenciaClaseAerobicaServicio = asistenciaClaseAerobicaServicio;
    }
    
    /*-------------------------------------------------------------------------------------
	 * 1. Registrar asistencia clase aerobica (api/asistencia-clase-aerobica/registrar)
	 *------------------------------------------------------------------------------------*/

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> registrarAsistenciaClaseAerobica(@Valid @RequestBody AsistenciaClaseAerobicaRequestDTO dto){

        AsistenciaClaseAerobica asistencia = asistenciaClaseAerobicaServicio.registrarAsistenciaClaseAerobica(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(asistenciaClaseAerobicaServicio.toResponseDTO(asistencia));
    }	

    /*-------------------------------------------------------------------------------------
	 * 2. Actualizar asistencia clase aerobica (api/asistencia-clase-aerobica/{id})
	 *------------------------------------------------------------------------------------*/

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> actualizarAsistenciaClaseAerobica(@PathVariable Integer id, @RequestBody AsistenciaClaseAerobicaActualizarDTO dto){
    
        AsistenciaClaseAerobica asistencia = asistenciaClaseAerobicaServicio.actualizarAsistenciaClaseAerobica(id, dto);
        return ResponseEntity.ok(asistenciaClaseAerobicaServicio.toResponseDTO(asistencia));
    }

    /*-------------------------------------------------------------------------------------
     * 3. Obtener asistencia clase aerobica por ID (api/asistencia-clase-aerobica/{id})
     *------------------------------------------------------------------------------------*/

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> obtenerAsistenciaPorId(@PathVariable Integer id) {
        AsistenciaClaseAerobica asistencia = asistenciaClaseAerobicaServicio.obtenerAsistenciaPorId(id);
        return ResponseEntity.ok(asistenciaClaseAerobicaServicio.toResponseDTO(asistencia));
    }

    /*------------------------------------------------------------------------------------------------------------
     * 4. Buscar asistencias de clase aerobica por fecha, mes o rango de fechas (api/asistencia-clase-aerobica)
     *-----------------------------------------------------------------------------------------------------------*/

    // Busca asistencias de clase aeróbica por fecha.
    @GetMapping(params = "fecha")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> buscarAsistenciasPorFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
            return ResponseEntity.ok(asistenciaClaseAerobicaServicio.toResponseDTO(asistenciaClaseAerobicaServicio.obtenerAsistenciasPorFecha(fecha)));
    }

    // Busca asistencias de clase aeróbica por mes y año.
    @GetMapping(params = {"anio", "mes"})
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> buscarPorMes(@RequestParam int anio, @RequestParam int mes){
        return ResponseEntity.ok(asistenciaClaseAerobicaServicio.toResponseDTO(asistenciaClaseAerobicaServicio.obtenerAsistenciasPorMes(anio, mes)));
    }

    // Busca asistencias de clase aeróbica por un rango de fechas.
    @GetMapping(params = {"inicio", "fin"})
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> buscarPorRangoFechas(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio, 
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin){
        return ResponseEntity.ok(asistenciaClaseAerobicaServicio.toResponseDTO(asistenciaClaseAerobicaServicio.obtenerAsistenciasPorRangoFechas(inicio, fin))); 
    }
}
