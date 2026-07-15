package com.cfp.mapa.controller;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.dto.metricas.AuditoriaAnaliticaResponseDTO;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.service.AuditoriaService;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auditorias")
public class AuditoriaController {

  private final AuditoriaService auditoriaService;

  @PreAuthorize("hasRole('OWNER')")
  @GetMapping
  public ResponseEntity<Page<AuditoriaResponseDTO>> listarHistorial(
      @RequestParam(required = false) Long usuarioId,
      @RequestParam(required = false) List<TipoAccionAuditoria> accion,
      @RequestParam(required = false) Long reporteId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "desc") String sort
  ) {

    Page<AuditoriaResponseDTO> historial = auditoriaService.listarHistorialPaginado(
        usuarioId,
        accion,
        reporteId,
        page,
        size,
        sort
    );

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(historial);
  }

  @PreAuthorize("hasRole('OWNER')")
  @GetMapping("/analitica")
  public ResponseEntity<AuditoriaAnaliticaResponseDTO> obtenerAnaliticaEntreFechas(
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate desde,
      @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate hasta,
      @RequestParam(defaultValue = "5") @Min(1) long topZonasCriticas
  ) {

    AuditoriaAnaliticaResponseDTO response = auditoriaService.obtenerAnaliticaEntreFechas(
        desde,
        hasta,
        topZonasCriticas);

    return  ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }
}