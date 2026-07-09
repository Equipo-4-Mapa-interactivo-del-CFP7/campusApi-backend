package com.cfp.mapa.dto.auditoria;

import java.time.LocalDateTime;

public record AuditoriaReporteCreadoDTO (

    Integer minutosEstimados,
    LocalDateTime fechaVencimientoNueva

) {

}
