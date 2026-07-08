package com.cfp.mapa.exception;

public class SolicitudIncorrectaException extends RuntimeException {

  /// HTTP Status: 400 Bad Request
  public SolicitudIncorrectaException(String message) {
    super(message);
  }
}
