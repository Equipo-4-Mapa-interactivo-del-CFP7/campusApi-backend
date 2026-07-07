package com.cfp.mapa.exception;

public class AuditoriaAnonimizacionException extends RuntimeException {

  /// HTTP Status: 500 Internal Server Error
  public AuditoriaAnonimizacionException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
