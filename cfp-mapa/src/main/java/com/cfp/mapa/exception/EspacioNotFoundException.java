package com.cfp.mapa.exception;

public class EspacioNotFoundException extends RuntimeException {

    /// HTTP Status: 404 Not Found
    public EspacioNotFoundException(Long id)
    {
        super("Espacio no encontrado con ID: " + id);
    }
}
