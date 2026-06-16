package com.cfp.mapa.controller;

import com.cfp.mapa.dto.espacio.*;
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
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EspacioController {

    private final EspacioService espacioService;

    // =========================
    // ADMIN
    // =========================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EspacioResponseDTO>> listarEspacios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) TipoEspacio tipo,
            @RequestParam(required = false) Boolean accesible,
            @RequestParam(required = false) Boolean activo,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<EspacioResponseDTO> espacios = espacioService.listarEspacios(
                nombre, descripcion, tipo, accesible, activo, pageable
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(espacios);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EspacioResponseDTO> actualizarEspacio(@PathVariable Long id, @RequestBody EspacioUpdateDTO dto) {
        EspacioResponseDTO espacioActualizado = espacioService.actualizarEspacio(id, dto);
        return ResponseEntity.ok(espacioActualizado);
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activarEspacio(@PathVariable Long id) {
        espacioService.activarEspacio(id);
        return ResponseEntity.ok("Espacio activado correctamente");

    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> desactivarEspacio(@PathVariable Long id) {
        espacioService.desactivarEspacio(id);
        return ResponseEntity.ok("Espacio desactivado correctamente");

    }

    // =========================
    // PERSONAL
    // =========================

    @GetMapping("/mapa")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<List<EspacioMapaDTO>> obtenerMapa(@RequestParam(required = false) TipoEspacio tipo) {
        List<EspacioMapaDTO> espacios = espacioService.obtenerMapa(tipo);
        return ResponseEntity.ok(espacios);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<EspacioDetalleDTO> obtenerPorId(@PathVariable Long id) {
        EspacioDetalleDTO espacio = espacioService.obtenerEspacioPorId(id);
        return ResponseEntity.ok(espacio);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<List<EspacioMapaDTO>> buscarEspacios(@RequestParam String nombre) {
        List<EspacioMapaDTO> espacios = espacioService.buscarEspacios(nombre);
        return ResponseEntity.ok(espacios);
    }

}
