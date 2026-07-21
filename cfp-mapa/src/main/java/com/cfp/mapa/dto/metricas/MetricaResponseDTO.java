package com.cfp.mapa.dto.metricas;

import java.time.LocalDate;

public record MetricaResponseDTO(
    LocalDate fechaDesde,
    LocalDate fechaHasta,
    CuentasDTO cuentas,
    DatosDTO datos,
    ReportesDTO reportes,
    UsuariosReportesDTO usuariosReportes,
    BusquedasMapaDTO busquedasMapa
) {

}
