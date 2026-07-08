package com.cfp.mapa.exception;

public class DniNotFoundException extends RuntimeException {

  /// HTTP Status: 404 Not Found
  public DniNotFoundException(String dni) {
    super("No se encontró un usuario con el DNI: " + dni);
  }
}
