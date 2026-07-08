package com.cfp.mapa.dto.auditoria;

import com.cfp.mapa.model.enums.Rol;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuditoriaUsuariosDetallesDTO(

    Long usuarioAfectadoId,
    Rol rolAnterior,
    Rol rolNuevo,
    String dniAnterior,
    String dniNuevo,
    String nombreAnterior,
    String nombreNuevo,
    String apellidoAnterior,
    String apellidoNuevo,
    Boolean activoAnterior,
    Boolean activoNuevo
) {

}
