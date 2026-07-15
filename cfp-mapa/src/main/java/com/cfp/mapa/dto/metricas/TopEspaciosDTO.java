package com.cfp.mapa.dto.metricas;

import com.cfp.mapa.model.enums.TipoReporte;
import java.util.List;

public record TopEspaciosDTO(

    TipoReporte tipo,
    List<EspacioDTO> zonas

) {

}
