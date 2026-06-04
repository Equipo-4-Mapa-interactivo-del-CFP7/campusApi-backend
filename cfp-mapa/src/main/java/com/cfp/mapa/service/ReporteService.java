package com.cfp.mapa.service;

import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReporteService {
//    List<Reporte> listarTodos();
//    List<Reporte> listarPorEspacio(Long espacioId);
//    List<Reporte> listarPorEstado(EstadoReporte estado);
//    Reporte crear(Reporte reporte);
//    Reporte actualizarEstado(Long id, EstadoReporte nuevoEstado);
    ReporteResponseDTO crearReporte(ReporteCreateRequestDTO request);

    Page<ReporteResponseDTO> listarReporteConFiltro(
            Long id,
            String estado,
            String tipoReporte,
            Pageable pageable
    );
}
