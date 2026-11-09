package com.app.gym.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.gym.modelos.ProductoStockBajo;

public interface ProductoStockBajoRepositorio extends JpaRepository<ProductoStockBajo, Integer> {
}
