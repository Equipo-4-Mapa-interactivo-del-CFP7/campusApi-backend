package com.cfp.mapa.dto.metricas;

import java.time.LocalDate;

public record AuditoriaAnaliticaResponseDTO(

    LocalDate fechaDesde,
    LocalDate fechaHasta,
    CuentasDTO cuentas,
    DatosDTO datos,
    ReportesDTO reportes

) {

}
