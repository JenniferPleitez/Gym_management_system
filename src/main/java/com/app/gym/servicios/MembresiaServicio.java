package com.app.gym.servicios;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.membresia.MembresiaActualizarDTO;
import com.app.gym.dtos.membresia.MembresiaRequestDTO;
import com.app.gym.dtos.membresia.MembresiaResponseDTO;
import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
import com.app.gym.modelos.Membresia;
import com.app.gym.repositorios.MembresiaRepositorio;
import com.app.gym.utils.ListUtils;
import com.app.gym.utils.converter.DuracionConverter;

@Service
public class MembresiaServicio {

    MembresiaRepositorio membresiaRepositorio;

    public MembresiaServicio (MembresiaRepositorio membresiaRepositorio){
        this.membresiaRepositorio = membresiaRepositorio;
    }

    // ------------------------------------------------------------------
    // Operación de creación
    // ------------------------------------------------------------------

    @Transactional
    public Membresia registrarMembresia (MembresiaRequestDTO dto){

        Duration duration = DuracionConverter.toDuration(dto.getCantidad(), dto.getUnidad());

        membresiaRepositorio.findByNombre(dto.getNombre().toLowerCase())
            .ifPresent(m -> { throw new RuntimeException("Ya existe una membresia con el nombre: " + dto.getNombre()); });

        Membresia membresiaNueva = new Membresia();

        membresiaNueva.setNombre(dto.getNombre().trim().toLowerCase());
        membresiaNueva.setDuracion(duration);
        membresiaNueva.setPrecio(dto.getPrecio());
        
        return membresiaRepositorio.save(membresiaNueva);
    }

    // ------------------------------------------------------------------
    // Operaciones de actualización
    // ------------------------------------------------------------------

    @Transactional
    public Membresia actualizarMembresia(Integer id, MembresiaActualizarDTO dto){

        Membresia membresia = membresiaRepositorio.findById(id)
                                        .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No se ha encontrado una membresia con el ID: " + id));

        if (dto.getNombre() != null) {
            // Verificar si el nombre ya existe (excluyendo la membresia actual)
            membresiaRepositorio.findByNombre(dto.getNombre().toLowerCase())
                    .ifPresent(m -> {
                        if (!m.getIdMembresia().equals(id)) {
                            throw new RuntimeException("Ya existe una membresia con el nombre: " + dto.getNombre());
                        }
                    });
            membresia.setNombre(dto.getNombre().trim().toLowerCase());
        }

        if (dto.getCantidad() != null || dto.getUnidad() != null) {
            
            if (dto.getCantidad() == null || dto.getUnidad() == null) {
                throw new IllegalArgumentException("Para actualizar la duración, deben proporcionarse tanto la cantidad como la unidad.");
            }
            
            if (dto.getCantidad() < 1) {
                throw new IllegalArgumentException("La cantidad debe ser al menos 1");
            }
            
            try {
                Duration nuevaDuracion = DuracionConverter.toDuration(dto.getCantidad(), dto.getUnidad());
                if (!nuevaDuracion.equals(membresia.getDuracion())) {
                    membresia.setDuracion(nuevaDuracion);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unidad no válida: " + dto.getUnidad() + ". Use 'dias', 'meses' o 'años'.");
            }
        }

        if(dto.getPrecio() != null){
            membresia.setPrecio(dto.getPrecio());
        }

        if(dto.getAnulada() != null){
            membresia.setAnulada(dto.getAnulada());
        }

        return membresiaRepositorio.save(membresia);
    } 

    // ------------------------------------------------------------------
    // Operaciones de consulta
    // ------------------------------------------------------------------

    // Obtener membresia por ID
    @Transactional(readOnly = true)
    public Membresia obtenerMembresiaPorId(Integer id){
        return membresiaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("No se ha encontrado una membresia con el ID: " + id));
    }

    // Obtener todas las membresias
    @Transactional(readOnly = true)
    public List<Membresia> obtenerMembresias(){
        return ListUtils.emptyIfNull(membresiaRepositorio.findAll());
    }

    // método para convertir una entidad Membresia a un DTO de respuesta
    public MembresiaResponseDTO toResponseDTO(Membresia membresia) {

        MembresiaResponseDTO dto = new MembresiaResponseDTO();
        
        dto.setIdMembresia(membresia.getIdMembresia());
        dto.setNombre(membresia.getNombre());
        //dto.setDuracion(/*FechaValidador.formatPeriod(*/membresia.getDuracion()/*)*/); // Convertir Duration a String legible
        dto.setDuracion(DuracionConverter.toRepresentacionAmigable(membresia.getDuracion()));
        dto.setPrecio(membresia.getPrecio());

        return dto;
    }

    // método para convertir una lista de entidades Membresia a una lista de DTOs de respuesta
    public List<MembresiaResponseDTO> toResponseDTO (List<Membresia> membresias) {
        return membresias.stream().map(this::toResponseDTO).toList();
    }
}