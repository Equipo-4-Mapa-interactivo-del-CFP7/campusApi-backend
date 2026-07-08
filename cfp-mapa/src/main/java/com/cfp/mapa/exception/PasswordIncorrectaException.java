package com.cfp.mapa.exception;

public class PasswordIncorrectaException extends RuntimeException {

  /// HTTP Status: 400 Bad Request
  public PasswordIncorrectaException() {
    super("La contraseña ingresada es incorrecta");
  }

  /// HTTP Status: 400 Bad Request
  public  PasswordIncorrectaException(String mensage) {
    super(mensage);
  }
}
