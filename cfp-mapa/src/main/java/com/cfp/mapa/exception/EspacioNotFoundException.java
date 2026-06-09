package com.cfp.mapa.exception;

public class EspacioNotFoundException extends RuntimeException {

    public EspacioNotFoundException(Long id)
    {
        super("Espacio no encontrado con ID: " + id);
    }
}
