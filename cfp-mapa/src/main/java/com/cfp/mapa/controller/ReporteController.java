package com.cfp.mapa.controller;

import com.cfp.mapa.dto.reporte.ReporteConteoDTO;
import com.cfp.mapa.dto.reporte.ReporteUpdateRequestDTO;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import com.cfp.mapa.service.ReporteService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'PERSONAL')")
    @PostMapping
    public ResponseEntity<ReporteResponseDTO> crearReporte(
            @Valid @RequestBody ReporteCreateRequestDTO request
    ) {

        ReporteResponseDTO response = reporteService.crearReporte(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'PERSONAL')")
    @PostMapping("/{id}/atender")
    public ResponseEntity<ReporteResponseDTO> atenderReporte(
        @PathVariable Long id,
        @Valid @RequestBody ReporteUpdateRequestDTO request
    ) {

        ReporteResponseDTO response = reporteService.atenderReporte(id, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'PERSONAL')")
    @PostMapping("/{id}/resolver")
    public ResponseEntity<ReporteResponseDTO> resolverReporte(
        @PathVariable Long id
    ) {

        ReporteResponseDTO response = reporteService.resolverReporte(id);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'PERSONAL')")
    @GetMapping
    public ResponseEntity<Page<ReporteResponseDTO>> listarReportes(
        @RequestParam(required = false) Long espacioId,
        @RequestParam(required = false) List<EstadoReporte> estado,
        @RequestParam(required = false) List<TipoReporte> tipo,
        @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Page<ReporteResponseDTO> reportes = reporteService.listarReporteConFiltro(
            espacioId, estado, tipo, pageable
        );

        return ResponseEntity.ok(reportes);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'PERSONAL')")
    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> obtenerReporte (
        @PathVariable Long id
    ) {

        ReporteResponseDTO response = reporteService.obtenerReporte(id);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'PERSONAL')")
    @GetMapping("/conteo")
    public ResponseEntity<ReporteConteoDTO> obtenerConteoReportes() {

        ReporteConteoDTO response = reporteService.obtenerConteoReportes();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }
}

