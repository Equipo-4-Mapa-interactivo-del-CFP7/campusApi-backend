package com.cfp.mapa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReporteConflictException extends RuntimeException {
    public ReporteConflictException(String tipoReporte, String nombreEspacio) {
        super(String.format("Ya existe un reporte de tipo '%s' en el espacio '%s'", tipoReporte, nombreEspacio));
    }
}
