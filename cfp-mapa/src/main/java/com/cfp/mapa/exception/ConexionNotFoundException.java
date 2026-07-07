package com.cfp.mapa.exception;


public class ConexionNotFoundException extends RuntimeException {

    /// HTTP Status: 404 Not Found
    public ConexionNotFoundException(Long id) {
        super("No se encontró la conexión con id: " + id);
    }
}
