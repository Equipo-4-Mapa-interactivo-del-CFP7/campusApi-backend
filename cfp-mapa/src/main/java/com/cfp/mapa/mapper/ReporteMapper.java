package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.reporte.ReporteConteoDTO;
import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import com.cfp.mapa.repository.projection.ReporteConteoProjection;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ReporteMapper {

    // Reporte -> ReporteResponseDTO
    public ReporteResponseDTO ReporteToResponse(Reporte reporte) {

        String nombreCompleto = String.format(
            "%s %s",
            reporte.getAtendidoPor().getNombre(),
            reporte.getAtendidoPor().getApellido()
        );

           return new ReporteResponseDTO(
               reporte.getId(),
               reporte.getAtendidoPor().getId(),
               nombreCompleto,
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

    public TipoReporte strToTipoReporte(String tipo) {
        return TipoReporte.valueOf(tipo.toUpperCase());
    }

    // ReporteConteoProjection -> ReporteConteoDTO
    public ReporteConteoDTO conteoProjectionToConteoDTO(
        List<ReporteConteoProjection> reportesContados) {

        int accesoBloqueado = 0;
        int problemaSenaletica = 0;
        int barreraFisica = 0;
        int dificultadOrientacion = 0;
        int otros = 0;

        if (reportesContados == null || reportesContados.isEmpty()) {
            return new ReporteConteoDTO(0, 0, 0, 0, 0);
        }

        for (ReporteConteoProjection projection : reportesContados) {

            int cantidad = projection.getCantidad().intValue();

            switch (projection.getTipo()) {
                case ACCESO_BLOQUEADO -> accesoBloqueado = cantidad;
                case PROBLEMA_SENALETICA -> problemaSenaletica = cantidad;
                case BARRERA_FISICA -> barreraFisica = cantidad;
                case DIFICULTAD_ORIENTACION -> dificultadOrientacion = cantidad;
                case OTROS -> otros = cantidad;
            }
        }

        return new ReporteConteoDTO(
            accesoBloqueado,
            problemaSenaletica,
            barreraFisica,
            dificultadOrientacion,
            otros
        );
    }
}