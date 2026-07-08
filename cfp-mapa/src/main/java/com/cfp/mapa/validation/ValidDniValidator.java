package com.cfp.mapa.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDniValidator implements ConstraintValidator<ValidDni, String> {

  private static final String DNI_REGEX = "^[0-9]{7,9}$";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    if (value == null) {
      return true;
    }

    return value.trim().matches(DNI_REGEX);
  }
}
