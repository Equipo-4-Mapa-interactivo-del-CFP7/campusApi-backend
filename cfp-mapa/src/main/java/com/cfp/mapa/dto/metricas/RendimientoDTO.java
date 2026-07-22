package com.cfp.mapa.dto.metricas;

import com.cfp.mapa.model.enums.TipoReporte;

public record RendimientoDTO(

    TipoReporte tipo,
    long creados,
    long cerrados,
    long promedioMinutos,
    long maximoMinutos

) {

}
