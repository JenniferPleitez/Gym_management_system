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

import com.app.gym.dtos.asistenciaGeneral.AsistenciaGeneralActualizarDTO;
import com.app.gym.dtos.asistenciaGeneral.AsistenciaRequestDTO;
import com.app.gym.servicios.AsistenciaGeneralServicio;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/asistencia-general")
public class AsistenciaGeneralControlador {
    
    private final AsistenciaGeneralServicio asistenciaGeneralServicio;

    public AsistenciaGeneralControlador(AsistenciaGeneralServicio asistenciaGeneralServicio) {
        this.asistenciaGeneralServicio = asistenciaGeneralServicio;
    }

	/*---------------------------------------------------------------------
	 * 1. Registrar asistencia general (api/asistencia-general/registrar)
	 *--------------------------------------------------------------------*/

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> registrarAsistenciaGeneral (@Valid @RequestBody AsistenciaRequestDTO dto){
		return ResponseEntity.status(HttpStatus.CREATED).body(asistenciaGeneralServicio.toResponseDTO(asistenciaGeneralServicio.registrarAsistenciaGeneral(dto)));
	}

    /*-------------------------------------------------------------------------------------------------------------------------------------------------
     * 2. Actualizar asistencia general. (horaEntrada y anulada)    /api/asistencia-general/actualizar/1/2025-01-05   formato de la fecha: YYYY-MM-DD
     *-------------------------------------------------------------------------------------------------------------------------------------------------*/

    @PatchMapping("/{id}/{fecha}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> actualizarAsistenciaGeneral(@PathVariable Integer id, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                        @RequestBody AsistenciaGeneralActualizarDTO dto){

        return ResponseEntity.ok(asistenciaGeneralServicio.toResponseDTO(asistenciaGeneralServicio.actualizarAsistenciaGeneral(id, fecha, dto)));      
    }

    /*-----------------------------------------------------------------------------------------------------------------------------
     * 3. Buscar asistencia por PK(ID y fecha). /api/asistencia-general/{id}/{fecha}   formato de la fecha: YYYY-MM-DD
     *---------------------------------------------------------------------------------------------------------------------------*/

    @GetMapping("/{id}/{fecha}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> buscarPorIdFecha (@PathVariable Integer id, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
        return ResponseEntity.ok(asistenciaGeneralServicio.toResponseDTO(asistenciaGeneralServicio.buscarPorIdFecha(id, fecha)));
    }

    /*-----------------------------------------------------------------------------------------------------------------------------
     * 4. Busca todas las asistencias generales registradas en una fecha específica.    /api/asistencia-general?fecha=2025-01-01   
     *---------------------------------------------------------------------------------------------------------------------------*/

    @GetMapping(params = "fecha")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> buscarAsistenciasPorFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
        return ResponseEntity.ok(asistenciaGeneralServicio.toResponseDTO(asistenciaGeneralServicio.buscarPorFecha(fecha)));
    }

    /**---------------------------------------------------------------------------------------------------------------------------
     * 5. Busca todas las asistencias generales registradas en un mes específico.   /api/asistencia-general?anio=2025&mes=01 
     *---------------------------------------------------------------------------------------------------------------------------*/
    
    @GetMapping(params = {"anio", "mes"})
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> buscarPorMes(@RequestParam int anio, @RequestParam int mes){
        return ResponseEntity.ok(asistenciaGeneralServicio.toResponseDTO(asistenciaGeneralServicio.buscarPorMes(anio, mes)));
    }

    /**-------------------------------------------------------------------------------------------------------------------------------------------------
     * 6. Busca todas las asistencias generales registradas en un rango de fechas específico. /api/asistencia-general?inicio=2025-01-01&fin=2025-01-31 
     *-------------------------------------------------------------------------------------------------------------------------------------------------*/
    
    @GetMapping(params = {"inicio", "fin"})
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> buscarPorRangoFechas (@RequestParam @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate inicio, 
                                                    @RequestParam @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate fin){
        return ResponseEntity.ok(asistenciaGeneralServicio.toResponseDTO(asistenciaGeneralServicio.buscarPorRangoFechas(inicio, fin)));
    }
}