package com.cfp.mapa.dto.metricas;

import java.util.List;

public record ReportesDTO(

    long creados,
    long atentidos,
    long modificados,
    long cerradosManual,
    long cerradosAutomatico,
    List<RendimientoDTO> rendimientoPorTipo,
    List<TopEspaciosDTO> espaciosCriticosPorTipo

) {

}
