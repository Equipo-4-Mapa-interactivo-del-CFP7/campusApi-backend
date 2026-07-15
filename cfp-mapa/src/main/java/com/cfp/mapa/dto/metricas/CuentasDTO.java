package com.cfp.mapa.dto.metricas;

public record CuentasDTO(

    long creados,
    long eliminados,
    long passwordRestablecidas,
    long passwordCambiadas,
    long estadosModificados,
    long rolesModificados,
    long ownerRecuperaciones,
    long ownerTransferencias

) {

}
