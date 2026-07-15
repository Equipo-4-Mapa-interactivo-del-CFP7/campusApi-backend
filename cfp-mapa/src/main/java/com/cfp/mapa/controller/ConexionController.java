package com.cfp.mapa.controller;

import com.cfp.mapa.dto.conexion.ConexionMapaDTO;
import com.cfp.mapa.dto.conexion.ConexionResponseDTO;
import com.cfp.mapa.dto.conexion.ConexionUpdateDTO;
import com.cfp.mapa.model.enums.EstadoConexion;
import com.cfp.mapa.service.ConexionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conexiones")
@CrossOrigin(origins = "http://127.0.0.1:5500/")
@RequiredArgsConstructor
public class ConexionController {

    private final ConexionService conexionService;

    // =========================
    // ADMIN / CONSULTA
    // =========================

    @GetMapping
    public ResponseEntity<List<ConexionResponseDTO>> listarConexiones(){
        return ResponseEntity.ok(conexionService.listarConexiones());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','PERSONAL')")
    public ResponseEntity<ConexionResponseDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(conexionService.obtenerConexionPorId(id));
    }

    // =========================
    // ADMIN
    // =========================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ConexionResponseDTO> actualizarConexion(@PathVariable Long id, @RequestBody ConexionUpdateDTO dto){
        return ResponseEntity.ok(conexionService.actualizarConexion(id, dto));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam EstadoConexion estado) {
        conexionService.cambiarEstado(id, estado);
        return ResponseEntity.ok().build();
    }

    // =========================
    // MAPA
    // =========================

    @GetMapping("/mapa")
    public ResponseEntity<List<ConexionMapaDTO>> obtenerMapa() {

        return ResponseEntity.ok(conexionService.obtenerMapa());
    }
}
