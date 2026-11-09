package com.app.gym.mappers;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.gym.dtos.usuario.UsuarioActualizarDTO;
import com.app.gym.modelos.Account;
import com.app.gym.modelos.Membresia;
import com.app.gym.modelos.Usuario;

import jakarta.persistence.EntityManager;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioMapper {
    
    // Mapea los campos no nulos del DTO sobre la entidad existente

    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "fechaFinMembresia", ignore = true)
    @Mapping(target = "venta", ignore = true)
    @Mapping(target = "asistenciaGeneral", ignore = true)  
    @Mapping(source = "membresia", target = "membresia")
    @Mapping(source = "account", target = "account")
    void updateFromDto(UsuarioActualizarDTO dto, @MappingTarget Usuario usuario, @Context EntityManager em); // si el DTO tiene un campo nulo, no lo actualiza 

    // Helper para mapear desde Integer -> Membresia
    default Membresia mapIdToMembresia(Integer id, @Context EntityManager em) {
        return id == null ? null : em.getReference(Membresia.class, id);
    }

    default Account mapIdToAccount(Integer id, @Context EntityManager em){
        return id == null ? null : em.getReference(Account.class, id);
    }
}