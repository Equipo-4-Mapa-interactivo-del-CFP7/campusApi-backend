package com.cfp.mapa.exception;

public class ParametroAccionInvalidoException extends RuntimeException {

  /// HTTP Status: 400 Bad Request
  public ParametroAccionInvalidoException(String message) {
    super(message);
  }
}
