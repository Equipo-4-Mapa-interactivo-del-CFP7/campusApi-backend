package com.cfp.mapa.controller;

import com.cfp.mapa.dto.conexion.ConexionResponseDTO;
import com.cfp.mapa.dto.conexion.ConexionUpdateDTO;
import com.cfp.mapa.service.ConexionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conexiones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ConexionController {

    private final ConexionService conexionService;

    // =========================
    // ADMIN / CONSULTA
    // =========================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<List<ConexionResponseDTO>> listarConexiones(){
        return ResponseEntity.ok(conexionService.listarConexiones());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<ConexionResponseDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(conexionService.obtenerConexionPorId(id));
    }

    // =========================
    // ADMIN
    // =========================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConexionResponseDTO> actualizarConexion(@PathVariable Long id, @RequestBody ConexionUpdateDTO dto){
        return ResponseEntity.ok(conexionService.actualizarConexion(id, dto));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activarConexion(@PathVariable Long id){
        conexionService.activarConexion(id);
        return ResponseEntity.ok("Conexion activada correctamente");
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> desactivarConexion(@PathVariable Long id){
        conexionService.desactivarConexion(id);
        return ResponseEntity.ok("Conexion desactivada correctamente");
    }
}
