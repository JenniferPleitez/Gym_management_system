package com.app.gym.servicios;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.gym.dtos.producto.ProductoActualizarDTO;
import com.app.gym.dtos.producto.ProductoRequestDTO;
import com.app.gym.dtos.producto.ProductoResponseDTO;
import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
import com.app.gym.modelos.Producto;
import com.app.gym.repositorios.ProductoRepositorio;
import com.app.gym.utils.ListUtils;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoServicio {

    ProductoRepositorio productoRepositorio; 

    public ProductoServicio(ProductoRepositorio productoRepositorio){
        this.productoRepositorio = productoRepositorio;
    }

    // ------------------------------------------------------------------
    // Operaciones de creación
    // ------------------------------------------------------------------
    @Transactional
    public Producto registrarProducto (ProductoRequestDTO productoRequestDTO){

        productoRepositorio.findByNombre(productoRequestDTO.getNombre().toLowerCase())
            .ifPresent(p -> { throw new RuntimeException("Ya existe un producto con el nombre: " + productoRequestDTO.getNombre()); });

        Producto producto = new Producto();

        producto.setNombre(productoRequestDTO.getNombre().trim().toLowerCase());
        producto.setDescripcion(productoRequestDTO.getDescripcion().trim().toLowerCase());
        producto.setPrecio(productoRequestDTO.getPrecio());
        producto.setCantidadDisponible(productoRequestDTO.getCantidadDisponible());
        
        return productoRepositorio.save(producto);
    } 

    // ------------------------------------------------------------------
    // Operaciones de actualización
    // ------------------------------------------------------------------
    @Transactional
    public Producto actualizarProducto(Integer id, ProductoActualizarDTO dto) {
        
        Producto producto = productoRepositorio.findById(id)
                                            .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No se ha encontrado un producto con el ID: " + id));

        if(dto.getNombre() != null){
            producto.setNombre(dto.getNombre().trim().toLowerCase());
        }

        if(dto.getDescripcion() != null){
            producto.setDescripcion(dto.getDescripcion().trim().toLowerCase());
        }

        if(dto.getPrecio() != null){
            producto.setPrecio(dto.getPrecio());
        }  

        if(dto.getCantidadDisponible() != null){
            producto.setCantidadDisponible(dto.getCantidadDisponible());
        }

        if(dto.getAnulada() != producto.getAnulada()){
            producto.setAnulada(dto.getAnulada());
        }

        return productoRepositorio.save(producto);
    }

    // ------------------------------------------------------------------
    // Métodos de consulta
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)  
    public List<Producto> obtenerProductos(){
        return ListUtils.emptyIfNull(productoRepositorio.findAll());
    }

    // Obtiene un producto por su ID.
    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(Integer idProducto){
        return productoRepositorio.findByIdProducto(idProducto).orElseThrow(() -> new RecursoNoEncontradoExcepcion("No se ha encontrado un producto con el ID: " + idProducto));
    }

    // Busca productos por su nombre sin importar que esté en minúsculas o mayúsculas.
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductoPorNombre(String nombre){
        return ListUtils.emptyIfNull(productoRepositorio.findByNombreIgnoreCaseContaining(nombre));
    }

    // Métodos para convertir a DTOs
    public ProductoResponseDTO toResponseDTO(Producto producto){
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setCantidadDisponible(producto.getCantidadDisponible());
        dto.setAnulada(producto.getAnulada());
        return dto;
    }

    // Variante para listas
    public List<ProductoResponseDTO> toResponseDTO(List<Producto> productos) {
        return productos.stream().map(this::toResponseDTO).toList();
    }
}
