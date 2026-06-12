package com.cfp.mapa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReporteNotFoundException extends RuntimeException {
    public ReporteNotFoundException(Long id) {
        super(String.format("No se encontró el reporte '%s'", id));
    }
}
