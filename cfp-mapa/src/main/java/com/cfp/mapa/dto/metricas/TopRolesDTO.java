package com.cfp.mapa.dto.metricas;

import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;

public record TopRolesDTO(

    Rol rol,
    TipoAccionAuditoria tipoAccion,
    Long cantidad
) {

}
