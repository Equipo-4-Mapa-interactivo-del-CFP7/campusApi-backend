package com.cfp.mapa.exception;

public class DniNotFoundException extends RuntimeException {

  public DniNotFoundException(String dni) {
    super("No se encontró un usuario con el DNI: " + dni);
  }
}
