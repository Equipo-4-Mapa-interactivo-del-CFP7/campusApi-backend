package com.cfp.mapa.exception;

public class UsuarioNotFoundException extends RuntimeException {

  /// HTTP Status: 404 Not Found
  public UsuarioNotFoundException(Long id) {
    super(String.format("No se encontró un usuario con la id '%d'", id));
  }
}
