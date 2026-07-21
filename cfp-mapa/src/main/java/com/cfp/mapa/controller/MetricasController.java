package com.cfp.mapa.controller;

import com.cfp.mapa.dto.metricas.MetricaFilterDTO;
import com.cfp.mapa.dto.metricas.MetricaResponseDTO;
import com.cfp.mapa.service.MetricasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/metricas")
public class MetricasController {

  private final MetricasService metricasService;

  @PreAuthorize("hasRole('OWNER')")
  @GetMapping
  public ResponseEntity<MetricaResponseDTO> obtenerMetricaEntreFechas (
      @Valid MetricaFilterDTO filtro
  ) {

    MetricaResponseDTO response = metricasService.obtenerMetricaEntreFechas(
        filtro.desde(),
        filtro.hasta(),
        filtro.topEspaciosCriticos(),
        filtro.topUsuarios(),
        filtro.topRoles(),
        filtro.topBusquedas()
    );

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }
}
