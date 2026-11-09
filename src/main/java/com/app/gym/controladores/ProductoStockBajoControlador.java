package com.app.gym.controladores;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.gym.servicios.ProductoStockBajoServicio;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/productos-stock-bajo")
@PreAuthorize("hasAnyRole('ADMIN')")
public class ProductoStockBajoControlador {
    
    private final ProductoStockBajoServicio productoStockBajoServicio;

    public ProductoStockBajoControlador(ProductoStockBajoServicio productoStockBajoServicio) {
        this.productoStockBajoServicio = productoStockBajoServicio;
    }

    /*--------------------------------------------------------------------------------------
     * 1. Listar productos con stock bajo (10 unidades o menos). /api/productos-stock-bajo
     *--------------------------------------------------------------------------------------*/
    @GetMapping
    public ResponseEntity<?> listarProductosStockBajo(){
        return ResponseEntity.ok(productoStockBajoServicio.obtenerProductosStockBajo());
    }
}
