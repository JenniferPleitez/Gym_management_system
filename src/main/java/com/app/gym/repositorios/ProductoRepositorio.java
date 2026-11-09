package com.app.gym.repositorios;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.gym.modelos.Producto;

public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {

    //Busca un producto por su nombre.
    public List<Producto> findByNombreIgnoreCaseContaining (String nombre);

    //Busca un producto por su ID.
    public Optional<Producto> findByIdProducto (Integer idProducto);

    // Busca un producto por su nombre exacto.
    public Optional<Producto> findByNombre (String nombre);
}
