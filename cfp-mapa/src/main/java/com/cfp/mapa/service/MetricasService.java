package com.cfp.mapa.service;

import com.cfp.mapa.dto.metricas.MetricaResponseDTO;
import java.time.LocalDate;

public interface MetricasService {

  MetricaResponseDTO obtenerMetricaEntreFechas(
      LocalDate desde,
      LocalDate hasta,
      Long topEspaciosCriticos,
      Long topUsuarios,
      Long topRoles,
      Long topBusquedas
  );
}
