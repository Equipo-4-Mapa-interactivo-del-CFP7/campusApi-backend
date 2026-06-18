package com.cfp.mapa.controller;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auditorias")
public class AuditoriaController {

  private final AuditoriaService auditoriaService;

  @GetMapping
  @PreAuthorize("hasRole('OWNER')")
  public ResponseEntity<Page<AuditoriaResponseDTO>> listarHistorial(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size,
      @RequestParam(defaultValue = "desc") String sort
  ) {
    Page<AuditoriaResponseDTO> historial = auditoriaService.listarHistorialPaginado(page, size, sort);
    return ResponseEntity.ok(historial);
  }
}