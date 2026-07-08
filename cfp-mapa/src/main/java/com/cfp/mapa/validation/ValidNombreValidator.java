package com.cfp.mapa.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidNombreValidator implements ConstraintValidator<ValidNombre, String> {

  private static final String NOMBRE_REGEX = "^(?=.*[a-zA-ZáéíóúÁÉÍÓÚñÑ])[a-zA-ZáéíóúÁÉÍÓÚñÑ '-]{2,50}$";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    if (value == null) {
      return true;
    }

    return value.matches(NOMBRE_REGEX);
  }
}