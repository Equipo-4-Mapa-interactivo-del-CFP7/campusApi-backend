package com.cfp.mapa.exception;

public class RolInvalidoException extends RuntimeException {

  /// HTTP Status: 400 Bad Request
  public RolInvalidoException(String message) {
    super(message);
  }
}
