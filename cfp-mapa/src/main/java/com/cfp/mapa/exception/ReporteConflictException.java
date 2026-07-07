package com.cfp.mapa.exception;

public class ReporteConflictException extends RuntimeException {

    /// HTTP Status: 409 Conflict
    public ReporteConflictException(String tipoReporte, String nombreEspacio) {
        super(String.format("Ya existe un reporte de tipo '%s' en el espacio '%s'",
            tipoReporte, nombreEspacio));
    }
}
