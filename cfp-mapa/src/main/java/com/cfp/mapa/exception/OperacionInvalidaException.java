package com.cfp.mapa.exception;

public class OperacionInvalidaException extends RuntimeException {

  /// HTTP Status: 400 Bad Request
  public OperacionInvalidaException(String message) {
    super(message);
  }
}
