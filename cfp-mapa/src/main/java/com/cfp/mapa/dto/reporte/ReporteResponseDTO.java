package com.cfp.mapa.dto.reporte;

import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import java.time.LocalDateTime;

public record ReporteResponseDTO(
        Long id,
        Long espacioId,
        String nombreEspacio,
        String descripcion,
        EstadoReporte estadoReporte,
        TipoReporte tipoReporte,
        Integer minutosEstimados,
        LocalDateTime fechaVencimiento,
        LocalDateTime fechaCreacion
) {}