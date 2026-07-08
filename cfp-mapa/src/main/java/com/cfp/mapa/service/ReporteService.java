package com.cfp.mapa.service;

import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.dto.reporte.ReporteUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReporteService {

    ReporteResponseDTO crearReporte(ReporteCreateRequestDTO request);

    Page<ReporteResponseDTO> listarReporteConFiltro(
        Long espacioId,
        String estado,
        String tipoReporte,
        Pageable pageable
    );

    ReporteResponseDTO atenderReporte(Long id, ReporteUpdateRequestDTO request);

    ReporteResponseDTO resolverReporte(Long id);

    void cerrarReportesAutomaticamente();

    ReporteResponseDTO obtenerReporte(Long id);

}
