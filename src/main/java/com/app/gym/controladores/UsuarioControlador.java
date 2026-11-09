package com.app.gym.controladores;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.gym.dtos.usuario.UsuarioActualizarDTO;
import com.app.gym.dtos.usuario.UsuarioRequestDTO;
import com.app.gym.servicios.UsuarioServicio;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;


@RestController 
@RequestMapping("/api/usuarios")
public class UsuarioControlador {
    
    UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    /*--------------------------------------------------------------
     * 1. Registrar usuario. /api/usuarios/registrar
     *-------------------------------------------------------------*/

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody UsuarioRequestDTO dto, BindingResult br) {

        if(br.hasErrors()) {
            List<String> errores = br.getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).toList();
                return ResponseEntity.badRequest().body(Map.of("error", errores));
        }
        return ResponseEntity.ok(usuarioServicio.toResponseDTO(usuarioServicio.registrarUsuario(dto)));
    }

    /*--------------------------------------------------------------
     * 2. Actualizar usuario. /api/usuarios/{id}
     *-------------------------------------------------------------*/
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @RequestBody UsuarioActualizarDTO dto){
        return ResponseEntity.ok(usuarioServicio.toResponseDTO(usuarioServicio.actualizarUsuario(id, dto)));
    }

    /*--------------------------------------------------------------
     * 3. Buscar usuario por ID. /api/usuarios/{id}
     *-------------------------------------------------------------*/
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuarioPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioServicio.toResponseDTO(usuarioServicio.buscarPorId(id)));
    }

    /*----------------------------------------------------------------------------------------------------
     * 5. Buscar usuarios cuya membresía está por vencer en los próximos 7 días. /api/usuarios/por-vencer
     *---------------------------------------------------------------------------------------------------*/
    @GetMapping("/por-vencer")
    public ResponseEntity<?> buscarUsuariosPorVencerEnUnaSemana() {
        return ResponseEntity.ok(usuarioServicio.toResponseDTO(usuarioServicio.usuariosPorVencerEnUnaSemana()));
    }
}
