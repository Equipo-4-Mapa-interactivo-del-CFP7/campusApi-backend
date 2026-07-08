package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ReporteMapper {

    // Reporte -> ReporteResponseDTO
    public ReporteResponseDTO ReporteToResponse(Reporte reporte) {

           return new ReporteResponseDTO(
               reporte.getId(),
               reporte.getEspacio().getId(),
               reporte.getEspacio().getNombre(),
               reporte.getDescripcion(),
               reporte.getEstado(),
               reporte.getTipo(),
               reporte.getMinutosEstimados(),
               reporte.getFechaVencimiento(),
               reporte.getFechaCreacion()
        );
    }

    // ReporteCreateRequestDTO -> Reporte
    public Reporte createToReporte(ReporteCreateRequestDTO request, Espacio espacio, TipoReporte tipoReporte) {

        Integer minutosEstimados = null;
        LocalDateTime fechaVencimiento = null;

        if (request.minutosEstimados() != null) {
            minutosEstimados = request.minutosEstimados();
            fechaVencimiento = LocalDateTime.now().plusMinutes(minutosEstimados);
        }

        return Reporte.builder()
            .espacio(espacio)
            .tipo(tipoReporte)
            .descripcion(request.descripcion())
            .estado(EstadoReporte.PENDIENTE)
            .minutosEstimados(minutosEstimados)
            .fechaVencimiento(fechaVencimiento)
            .build();
    }

    public EstadoReporte strToEstadoReporte(String estado) {
        return EstadoReporte.valueOf(estado.toUpperCase());
    }

    public TipoReporte strToTipoReporte(String tipo) {
        return TipoReporte.valueOf(tipo.toUpperCase());
    }
}