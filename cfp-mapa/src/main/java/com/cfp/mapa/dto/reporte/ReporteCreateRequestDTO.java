package com.cfp.mapa.dto.reporte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReporteCreateRequestDTO(
        @Size(max = 100)
        @NotBlank
        String descripcion,

        @Size(max = 15)
        @NotBlank
        String tipoReporte
) {

}
