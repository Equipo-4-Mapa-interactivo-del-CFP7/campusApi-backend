package com.cfp.mapa.exception;

public class RutaNoEncontradaException extends RuntimeException {
    public RutaNoEncontradaException(Long origenId, Long destinoId) {
        super("No existe un recorrido entre los espacios " + origenId + " y " + destinoId);
    }
}
