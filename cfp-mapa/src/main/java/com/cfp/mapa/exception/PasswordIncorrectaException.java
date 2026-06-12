package com.cfp.mapa.exception;

public class PasswordIncorrectaException extends RuntimeException {

  public PasswordIncorrectaException() {
    super("La contraseña ingresada es incorrecta");
  }
}
