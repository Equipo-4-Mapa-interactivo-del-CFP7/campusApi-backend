package com.cfp.mapa.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDniValidator implements ConstraintValidator<ValidDni, String> {

  private static final String DNI_REGEX = "^[a-zA-Z0-9]{7,20}$";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    if (value == null) {
      return true;
    }

    return value.matches(DNI_REGEX);
  }
}
