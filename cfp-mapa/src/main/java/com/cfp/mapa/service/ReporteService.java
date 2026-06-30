package com.cfp.mapa.service;

import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.dto.reporte.ReporteUpdateRequestDTO;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReporteService {

    ReporteResponseDTO crearReporte(ReporteCreateRequestDTO request);

    Page<ReporteResponseDTO> listarReporteConFiltro(
            Long id,
            EstadoReporte estado,
            TipoReporte tipoReporte,
            Pageable pageable
    );

    ReporteResponseDTO actualizarEstado(Long id, ReporteUpdateRequestDTO request);

}
