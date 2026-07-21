package com.cfp.mapa.dto.metricas;

import java.util.List;

public record UsuariosReportesDTO(

    List<TopUsuariosDTO> topUsuarios,
    List<TopRolesDTO> topRoles
) {

}
