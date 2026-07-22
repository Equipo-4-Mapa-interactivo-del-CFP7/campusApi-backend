package com.cfp.mapa.dto.metricas;

import com.cfp.mapa.model.enums.Rol;

public record RolDTO(

    Rol rol,
    long cantidad
) {

}
