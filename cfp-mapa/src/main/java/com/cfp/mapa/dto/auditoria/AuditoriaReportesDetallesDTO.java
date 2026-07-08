package com.cfp.mapa.dto.auditoria;

import java.time.LocalDateTime;

public record AuditoriaReportesDetallesDTO(

    String descripcionAnterior,
    String descripcionNueva,
    LocalDateTime fechaVencimientoAnterior,
    LocalDateTime fechaVencimientoNueva

) {

}
