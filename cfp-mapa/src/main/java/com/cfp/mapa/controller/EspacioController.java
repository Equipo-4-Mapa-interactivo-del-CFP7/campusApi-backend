package com.cfp.mapa.controller;

import com.cfp.mapa.dto.espacio.*;
import com.cfp.mapa.model.enums.EstadoEspacio;
import com.cfp.mapa.model.enums.TipoEspacio;
import com.cfp.mapa.service.EspacioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/espacios")
@CrossOrigin(origins = "http://127.0.0.1:5500/")
@RequiredArgsConstructor
public class EspacioController {

    private final EspacioService espacioService;

    // =========================
    // ADMIN
    // =========================

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Page<EspacioResponseDTO>> listarEspacios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) TipoEspacio tipo,
            @RequestParam(required = false) Boolean accesible,
            @RequestParam(required = false) EstadoEspacio estado,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<EspacioResponseDTO> espacios = espacioService.listarEspacios(
                nombre, descripcion, tipo, accesible, estado, pageable
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(espacios);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<EspacioResponseDTO> actualizarEspacio(@PathVariable Long id, @RequestBody EspacioUpdateDTO dto) {
        EspacioResponseDTO espacioActualizado = espacioService.actualizarEspacio(id, dto);
        return ResponseEntity.ok(espacioActualizado);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody EstadoEspacio estado) {
        espacioService.cambiarEstado(id, estado);
        return ResponseEntity.ok("Estado actualizado correctamente");
    }

    // =========================
    // USUARIOS
    // =========================

    @GetMapping("/mapa")
    public ResponseEntity<List<EspacioMapaDTO>> obtenerMapa(@RequestParam(required = false) TipoEspacio tipo) {
        List<EspacioMapaDTO> espacios = espacioService.obtenerMapa(tipo);
        return ResponseEntity.ok(espacios);

    }

    @GetMapping("/{id}")
    public ResponseEntity<EspacioDetalleDTO> obtenerPorId(@PathVariable Long id) {
        EspacioDetalleDTO espacio = espacioService.obtenerEspacioPorId(id);
        return ResponseEntity.ok(espacio);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<EspacioMapaDTO>> buscarEspacios(@RequestParam String nombre) {
        List<EspacioMapaDTO> espacios = espacioService.buscarEspacios(nombre);
        return ResponseEntity.ok(espacios);
    }

}
