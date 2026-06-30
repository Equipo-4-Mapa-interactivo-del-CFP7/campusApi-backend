package com.cfp.mapa.exception;


public class ConexionNotFoundException extends RuntimeException {
    public ConexionNotFoundException(Long id) {
        super("No se encontró la conexión con id: " + id);
    }
}
