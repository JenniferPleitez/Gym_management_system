package com.app.gym.servicios;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.gym.modelos.ProductoStockBajo;
import com.app.gym.repositorios.ProductoStockBajoRepositorio;
import com.app.gym.utils.ListUtils;

@Service
public class ProductoStockBajoServicio {
    /**
     * Servicio para gestionar productos con stock bajo.
     * Proporciona métodos para obtener productos cuyo stock es igual o menor a 10 unidades.
     */

    private final ProductoStockBajoRepositorio repositorio;

    public ProductoStockBajoServicio(ProductoStockBajoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    // Obtener productos con stock bajo (10 unidades o menos)
    public List<ProductoStockBajo> obtenerProductosStockBajo() {
        return ListUtils.emptyIfNull(repositorio.findAll());
    }
}
