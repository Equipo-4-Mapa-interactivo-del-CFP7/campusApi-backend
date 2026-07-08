package com.cfp.mapa.exception;

public class ReporteNotFoundException extends RuntimeException {

    /// HTTP Status: 404 Not Found
    public ReporteNotFoundException(Long id) {
        super(String.format("No se encontró el reporte '%s'", id));
    }
}
