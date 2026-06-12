package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;

public class ReporteMapper {

    // Reporte -> ReporteResponseDTO
    public ReporteResponseDTO ReporteToResponse(Reporte reporte) {

        return new ReporteResponseDTO(
                reporte.getId(),
                reporte.getDescripcion(),
                reporte.getTipo().name(),
                reporte.getEstado().name()
        );
    }

    // ReporteCreateRequestDTO -> Reporte
    public Reporte createToReporte(ReporteCreateRequestDTO request, Long new_id) {
        return Reporte.builder()
                .id(new_id)
                .descripcion(request.descripcion())
                .estado(EstadoReporte.PENDIENTE)
                .tipo(strToTipoReporte(request.tipoReporte()))
                .build();
    }

    private EstadoReporte strToEstadoReporte(String estado) {
        estado.toUpperCase();
        if (estado == "PENDIENTE")
            return EstadoReporte.PENDIENTE;
        else if (estado == "EN_REVISION")
            return EstadoReporte.EN_REVISION;
        else if (estado == "RESUELTO")
            return EstadoReporte.RESUELTO;
        return null;
    }

    private TipoReporte strToTipoReporte(String tipo) {
        tipo.toUpperCase();
        if (tipo == "ACCESO_BLOQUEADO")
            return TipoReporte.ACCESO_BLOQUEADO;
        else if (tipo == "PROBLEMA_SENALETICA")
            return TipoReporte.PROBLEMA_SENALETICA;
        else if (tipo == "BARRERA_FISICA")
            return TipoReporte.BARRERA_FISICA;
        else if (tipo == "DIFICULTAD_ORIENTACION")
            return TipoReporte.DIFICULTAD_ORIENTACION;
        return null;
    }
}