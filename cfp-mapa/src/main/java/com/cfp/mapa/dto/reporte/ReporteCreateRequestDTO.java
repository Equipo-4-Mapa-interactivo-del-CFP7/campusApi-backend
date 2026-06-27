package com.cfp.mapa.dto.reporte;

import com.cfp.mapa.model.enums.TipoReporte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReporteCreateRequestDTO(

        @NotNull(message = "El tipo de reporte es obligatorio")
        TipoReporte tipoReporte,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 100, message = "La descripción no puede superar los 100 caracteres")
        String descripcion,

        @NotNull(message = "El espacio es obligatorio")
        Long espacioId,

        String imagenURL

) {}