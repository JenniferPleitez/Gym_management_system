package com.app.gym.controladores;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.gym.dtos.producto.ProductoActualizarDTO;
import com.app.gym.dtos.producto.ProductoRequestDTO;
import com.app.gym.servicios.ProductoServicio;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/productos")
public class ProductoControlador {
    
    ProductoServicio productoServicio;
    
    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    /*--------------------------------------------------------------
     * 1. Registrar producto. /api/productos/registrar
     *-------------------------------------------------------------*/

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> registrarProducto(@Valid @RequestBody ProductoRequestDTO dto){ {

            return ResponseEntity.ok(productoServicio.registrarProducto(dto));
        }
    }

    /*--------------------------------------------------------------
     * 2. Actualizar producto. /api/prodcutos/{id}
     *-------------------------------------------------------------*/
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> actualizarProducto(@PathVariable Integer id, @Valid @RequestBody ProductoActualizarDTO dto) {
        
        return ResponseEntity.ok(productoServicio.toResponseDTO(productoServicio.actualizarProducto(id, dto)));
    }

    /*--------------------------------------------------------------
     * 3. Obtener productos. /api/productos
     *-------------------------------------------------------------*/
    @GetMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> obtenerProductos() {
        return ResponseEntity.ok(productoServicio.toResponseDTO(productoServicio.obtenerProductos()));
    }

    /*--------------------------------------------------------------
     * 4. Buscar usuario por ID. /api/productos/{id}
     *-------------------------------------------------------------*/
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(productoServicio.toResponseDTO(productoServicio.obtenerProductoPorId(id)));
    }

    /*--------------------------------------------------------------
     * 5. Buscar usuario por email. /api/productos?email=ejemplo@gmail.com 
     *-------------------------------------------------------------*/
    @GetMapping(params = "nombre")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> buscarProductoPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoServicio.toResponseDTO(productoServicio.obtenerProductoPorNombre(nombre)));
    }
}
