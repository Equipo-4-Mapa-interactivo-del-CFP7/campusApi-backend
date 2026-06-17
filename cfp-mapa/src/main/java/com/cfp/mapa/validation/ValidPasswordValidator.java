package com.cfp.mapa.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

  private static final String PASSWORD_REGEX = "^\\S{8,60}$";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    if (value == null) {
      return true;
    }

    return value.matches(PASSWORD_REGEX);
  }
}