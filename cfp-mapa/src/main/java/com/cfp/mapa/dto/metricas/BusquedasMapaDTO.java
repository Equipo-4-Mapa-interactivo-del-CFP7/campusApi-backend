package com.cfp.mapa.dto.metricas;

import java.util.List;

public record BusquedasMapaDTO(
    long totalConsultas,
    List<EspacioBusquedaDTO> topOrigenes,
    List<EspacioBusquedaDTO> topDestinos,
    List<RutaBusquedaDTO> topRutas
) {

}
