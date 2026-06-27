package com.cfp.mapa.dto.reporte;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReporteUpdateRequestDTO(

        @NotBlank(message = "El estado es obligatorio")
        String estado,

        @Min(1)
        Integer minutosEstimados

) {
}
