package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import org.springframework.stereotype.Component;

@Component
public class ReporteMapper {

    // Reporte -> ReporteResponseDTO
    public ReporteResponseDTO ReporteToResponse(Reporte reporte) {

        return new ReporteResponseDTO(
                reporte.getId(),
                reporte.getEspacioId(),
                reporte.getDescripcion(),
                reporte.getEstado(),
                reporte.getTipo(),
                reporte.getUrlImagen()
        );
    }

    // ReporteCreateRequestDTO -> Reporte
    public Reporte createToReporte(ReporteCreateRequestDTO request) {
        return Reporte.builder()
                .tipo(request.tipoReporte())
                .descripcion(request.descripcion())
                .estado(EstadoReporte.PENDIENTE)
                .espacioId(request.espacioId())
                .urlImagen(request.imagenURL())
                .build();
    }

    public EstadoReporte strToEstadoReporte(String estado) {
        return EstadoReporte.valueOf(estado.toUpperCase());
    }

    public TipoReporte strToTipoReporte(String tipo) {
        return TipoReporte.valueOf(tipo.toUpperCase());
    }
}