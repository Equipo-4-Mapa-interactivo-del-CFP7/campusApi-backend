package com.cfp.mapa.dto.metricas;

import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import java.util.List;

public record TopRolesDTO(

    TipoAccionAuditoria tipoAccion,
    List<RolDTO> topRoles
) {

}
