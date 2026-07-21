package com.cfp.mapa.dto.metricas;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record MetricaFilterDTO(

    @NotNull(message = "La fecha de inicio es requerida")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate desde,

    @NotNull(message = "La fecha de fin es requerida")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate hasta,

    Long topEspaciosCriticos,
    Long topUsuarios,
    Long topRoles,
    Long topBusquedas
) {

}
