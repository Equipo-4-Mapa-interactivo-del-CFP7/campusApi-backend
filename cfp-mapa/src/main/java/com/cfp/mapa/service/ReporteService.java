package com.cfp.mapa.service;

<<<<<<< Updated upstream
import com.cfp.mapa.model.EstadoReporte;
=======
import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.model.enums.EstadoReporte;
>>>>>>> Stashed changes
import com.cfp.mapa.model.Reporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReporteService {
<<<<<<< Updated upstream
    List<Reporte> listarTodos();
    List<Reporte> listarPorEspacio(Long espacioId);
    List<Reporte> listarPorEstado(EstadoReporte estado);
    Reporte crear(Reporte reporte);
    Reporte actualizarEstado(Long id, EstadoReporte nuevoEstado);
=======
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
>>>>>>> Stashed changes
}
