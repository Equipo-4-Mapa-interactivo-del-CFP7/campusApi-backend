package com.cfp.mapa.validation;

import com.cfp.mapa.model.enums.TipoReporte;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidTipoReporteValidator implements ConstraintValidator<ValidTipoReporte, String> {

  private Set<String> tipoReportesValidos;

  @Override
  public void initialize(ValidTipoReporte constraintAnnotation) {

    this.tipoReportesValidos = Arrays.stream(TipoReporte.values())
        .map(TipoReporte::name)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    if (value == null || value.trim().isEmpty()) {
      return true;
    }

    return tipoReportesValidos.contains(value.toUpperCase().trim());
  }
}
