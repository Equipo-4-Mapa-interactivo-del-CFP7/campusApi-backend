package com.cfp.mapa.exception;

public class NombreInvalidoException extends RuntimeException {

  /// HTTP Status: 400 Bad Request
  public NombreInvalidoException(String message) {
    super(message);
  }
}
