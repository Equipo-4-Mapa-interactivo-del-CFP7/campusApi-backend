package com.cfp.mapa.exception;

public class AccionNoPermitidaException extends RuntimeException {

  /// HTTP Status: 403 Forbidden + errorCode
  public AccionNoPermitidaException(String message) {
    super(message);
  }
}
