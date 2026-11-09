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

import com.app.gym.dtos.venta.VentaActualizarDto;
import com.app.gym.dtos.venta.VentaCrearDto;
import com.app.gym.servicios.VentaServicio;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ventas")
public class VentaControlador {

    private final VentaServicio ventaServicio;

    public VentaControlador(VentaServicio ventaServicio) {
        this.ventaServicio = ventaServicio;
    }

    /*--------------------------------------------------------------
     * 1. Crear venta. /api/ventas/registrar
     *-------------------------------------------------------------*/
    
    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> registrarVenta(@Valid @RequestBody VentaCrearDto dto) { 

        return ResponseEntity.status(HttpStatus.CREATED).body(ventaServicio.toResponseDTO(ventaServicio.registrarVenta(dto)));
    }

    /*------------------------------------------------------------------------------------------------------------
     * 2. Actualiza una venta (cantidad / anular). /api/ventas/{id}/{fecha}     formato de la fecha: YYYY-MM-DD
     *-----------------------------------------------------------------------------------------------------------*/
    
    @PatchMapping("/{id}/{fecha}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> actualizarVenta(@PathVariable Integer id,
                                            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                            @Valid @RequestBody VentaActualizarDto dto) {

        return ResponseEntity.ok(ventaServicio.toResponseDTO(ventaServicio.actualizarVenta(id, fecha, dto)));
    }

    /*----------------------------------------------------------------------------
     * 3. Buscar venta por PK (ID y fecha).      /api/ventas/{id}/{fecha}   
     *----------------------------------------------------------------------------*/
    
    @GetMapping("/{id}/{fecha}") 
    @PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<?> buscarVentaPorIdFecha(@PathVariable Integer id, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
        return ResponseEntity.ok(ventaServicio.toResponseDTO(ventaServicio.buscarPorIdFecha(id, fecha))); 
	}

    /*--------------------------------------------------------------------------------------------------
     * 4. Busca todas las ventas realizadas en una fecha específica.   /api/ventas?fecha=2025-01-01 
     *------------------------------------------------------------------------------------------------*/
    
    @GetMapping(params = "fecha") 
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> buscarVentasPorFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
        return ResponseEntity.ok(ventaServicio.toResponseDTO(ventaServicio.buscarPorFecha(fecha)));
    }

    /*-----------------------------------------------------------------------------------------------
     * 5. Busca todas las ventas realizadas en un mes específico.   /api/ventas?anio=2025&mes=01    
     *----------------------------------------------------------------------------------------------*/
    
    @GetMapping(params = {"anio", "mes"})
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> buscarPorMes(@RequestParam int anio, @RequestParam int mes){
        return ResponseEntity.ok(ventaServicio.toResponseDTO(ventaServicio.buscarPorMes(anio, mes)));
    }

    /*----------------------------------------------------------------------------------------------------------------
     * 6. Busca todas las ventas realizadas en un rango de fechas.     /api/ventas?inicio=2025-01-01&fin=2025-01-31   
     *----------------------------------------------------------------------------------------------------------------*/
    
    @GetMapping(params = {"inicio", "fin"})
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> buscarPorRangoFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
												@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin){
        return ResponseEntity.ok(ventaServicio.toResponseDTO(ventaServicio.buscarPorRangoFechas(inicio, fin)));
    }
}