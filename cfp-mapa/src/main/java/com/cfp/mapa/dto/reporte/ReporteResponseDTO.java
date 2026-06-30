package com.cfp.mapa.dto.reporte;

import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;

public record ReporteResponseDTO(
        Long id,
        Long espacioId,
        String descripcion,
        EstadoReporte estadoReporte,
        TipoReporte tipoReporte,
        String urlFoto
) {}