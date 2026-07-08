package com.cfp.mapa.dto.reporte;

import com.cfp.mapa.validation.ValidTipoReporte;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReporteCreateRequestDTO(

        @NotBlank(message = "El tipo de reporte es obligatorio")
        @ValidTipoReporte
        String tipoReporte,

        @Size(max = 100, message = "La descripción no puede superar los 100 caracteres")
        String descripcion,

        @NotNull(message = "El espacio es obligatorio")
        Long espacioId,

        @Min(value = 1, message = "Los minutos estimados deben ser mayores a 0")
        Integer minutosEstimados

) {}