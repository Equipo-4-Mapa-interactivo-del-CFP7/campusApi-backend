package com.cfp.mapa.controller;

import com.cfp.mapa.service.ReporteService;
import org.springframework.http.ResponseEntity;
import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReportUpdateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping("/reportar")
    public ResponseEntity<ReporteResponseDTO> reportarIncidencia(
            @Valid @RequestBody ReporteCreateRequestDTO request
    ) {
        ReporteResponseDTO response = reporteService.crearReporte(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReporteResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ReporteUpdateRequestDTO request
    ) {
        ReporteResponseDTO response = reporteService.actualizarEstado(id, request);
        return ResponseEntity.ok(response);
    }

}

