package com.cfp.mapa.validation;

import com.cfp.mapa.model.enums.Rol;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidRolValidator implements ConstraintValidator<ValidRol, String> {

  private Set<String> rolesValidos;

  @Override
  public void initialize(ValidRol constraintAnnotation) {

    this.rolesValidos = Arrays.stream(Rol.values())
        .map(Rol::name)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    if (value == null || value.trim().isEmpty()) {
      return true;
    }

    return rolesValidos.contains(value.toUpperCase().trim());
  }
}