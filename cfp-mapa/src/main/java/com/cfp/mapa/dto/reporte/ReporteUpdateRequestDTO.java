package com.cfp.mapa.dto.reporte;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ReporteUpdateRequestDTO(

    @Min(value = 1, message = "Los minutos estimados deben ser mayores a 0")
    Integer minutosEstimados,

    Boolean quitarContador,

    @Size(max = 100, message = "La descripción no puede superar los 100 caracteres")
    String descripcion

) {
}
