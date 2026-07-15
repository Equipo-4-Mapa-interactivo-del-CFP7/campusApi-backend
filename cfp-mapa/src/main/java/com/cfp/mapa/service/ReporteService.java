package com.cfp.mapa.service;

import com.cfp.mapa.dto.reporte.ReporteConteoDTO;
import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.dto.reporte.ReporteUpdateRequestDTO;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReporteService {

    ReporteResponseDTO crearReporte(ReporteCreateRequestDTO request);

    Page<ReporteResponseDTO> listarReporteConFiltro(
        Long espacioId,
        List<EstadoReporte> estado,
        List<TipoReporte> tipoReporte,
        Pageable pageable
    );

    ReporteResponseDTO atenderReporte(Long id, ReporteUpdateRequestDTO request);

    ReporteResponseDTO resolverReporte(Long id);

    void cerrarReportesAutomaticamente();

    ReporteResponseDTO obtenerReporte(Long id);

    ReporteConteoDTO obtenerConteoReportes();

}
