package com.cfp.mapa.dto.metricas;

import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import java.util.List;

public record TopUsuariosDTO(

    TipoAccionAuditoria tipoAccion,
    List<UsuarioDTO> topUsuarios
) {

}
