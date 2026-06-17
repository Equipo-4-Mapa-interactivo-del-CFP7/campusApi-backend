package com.cfp.mapa.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidApellidoValidator implements ConstraintValidator<ValidApellido, String> {

  private static final String APELLIDO_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ '-]{2,50}$";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;
    return value.matches(APELLIDO_REGEX);
  }
}