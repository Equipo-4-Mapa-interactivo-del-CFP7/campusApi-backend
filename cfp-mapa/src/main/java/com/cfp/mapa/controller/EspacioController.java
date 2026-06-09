package com.cfp.mapa.controller;

import com.cfp.mapa.dto.espacio.EspacioDetalleDTO;
import com.cfp.mapa.dto.espacio.EspacioMapaDTO;
import com.cfp.mapa.dto.espacio.EspacioResponseDTO;
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

    // =========================
    // TODO: tengo que terminar todo esto.
    // =========================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EspacioResponseDTO> crearEspacio(){

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EspacioResponseDTO> actualizarEspacio(){

    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activarEspacio(){

    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> desactivarEspacio(){

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarEspacio(){

    }

    // =========================
    // PERSONAL
    // =========================

    @GetMapping("/mapa")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<List<EspacioMapaDTO>> obtenerMapa(){

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<EspacioDetalleDTO> obtenerPorId(){

    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<List<EspacioResponseDTO>> buscarEspacios(){

    }

}
