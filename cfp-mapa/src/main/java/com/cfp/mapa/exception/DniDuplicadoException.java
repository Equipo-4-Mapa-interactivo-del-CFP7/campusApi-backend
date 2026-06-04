package com.cfp.mapa.exception;

public class DniDuplicadoException extends RuntimeException {

  public DniDuplicadoException(String dni) {
    super(String.format("Ya existe un usuario con el dni '%s'", dni));
  }
}
