package com.cfp.mapa.exception;

public class DniDuplicadoException extends RuntimeException {

  /// HTTP Status: 409 Conflict
  public DniDuplicadoException(String dni) {
    super(String.format("Ya existe un usuario con el dni '%s'", dni));
  }
}
