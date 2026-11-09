package com.app.gym.mappers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.app.gym.dtos.account.AccountResponseDTO;
import com.app.gym.dtos.auth.CacheAccountDTO;
import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
import com.app.gym.modelos.Account;
import com.app.gym.modelos.Role;
import com.app.gym.repositorios.RoleRepositorio;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class AccountMapper {

    @Autowired
    private RoleRepositorio roleRepositorio;
    
    // ---------------------------------------------------------
    // 1. READ: From Entity to Response DTO 
    // ---------------------------------------------------------
    /*@Mapping(source = "idAccount", target = "idAccount")
    @Mapping(source = "publicId", target = "publicId")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "emailVerified", target = "emailVerified")
    @Mapping(source = "enabled", target = "enabled")
    @Mapping(source = "locked", target = "locked")*/
    @Mapping(target = "roles", expression = "java(mapRoles(account.getRoles()))")
    public abstract AccountResponseDTO toResponseDto(Account account);


    // ---------------------------------------------------------
    // 2. READ: From Entities to response list.
    // ---------------------------------------------------------
    public abstract List<AccountResponseDTO> toResponseDto(List<Account> account);

    // DTO DE CACHÉ A ACCOUNT
    //@Mapping(target = "roles", expression = "java(mapRolesToAccount(cacheAccountDTO.roles()))")
    /*@Mapping(source = "idAccount", target = "idAccount")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "emailVerified", target = "emailVerified")
    @Mapping(source = "enabled", target = "enabled")
    @Mapping(source = "locked", target = "locked")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "lastLogin", target = "lastLogin")
    @Mapping(source = "tokenVersion", target = "tokenVersion")
    @Mapping(source = "publicId", target = "publicId")
    @Mapping(source = "roles", target = "roles")*/
    public abstract Account cacheToAccount(CacheAccountDTO cacheAccountDTO);

    // ACCOUNT A CACHÉ DTO
    @Mapping(target = "roles", expression = "java(mapRoles(account.getRoles()))")
    public abstract CacheAccountDTO accountToCache(Account account);

    // UPDATE LASTLOGIN
    @Mapping(target = "lastLogin", source = "lastLogin")
    public abstract CacheAccountDTO updateLastLogin(CacheAccountDTO original, OffsetDateTime lastLogin);

     // ---------------------------------------------------------
    // 4. Métodos personalizados para lógica compleja
    // ---------------------------------------------------------
    protected List<String> mapRoles(Set<Role> roles) {
        if (roles == null) return List.of();
        return roles.stream()
                .map(Role::getRoleName)
                .sorted()
                .collect(Collectors.toList());
    }

    /*protected Set<Role> mapRolesToAccount(List<String> rolesStr ) {
        Set<Role> set = new HashSet<>();
        for(String roleStr : new HashSet<>(rolesStr)){
            Role role = roleRepositorio.findByRoleName(roleStr).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Role not found: " + roleStr));
            set.add(role);
        }

        return set;
    }*/

    protected Role mapRolesToAccount(String roleStr) {
        return roleRepositorio.findByRoleName(roleStr).
            orElseThrow(() -> new RecursoNoEncontradoExcepcion("Role not found: " + roleStr) );
    }
}
