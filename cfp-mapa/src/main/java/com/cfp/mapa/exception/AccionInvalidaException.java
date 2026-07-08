package com.cfp.mapa.exception;

public class AccionInvalidaException extends RuntimeException {

  /// HTTP Status: 403 Forbidden
  public AccionInvalidaException(String message) {
    super(message);
  }
}
