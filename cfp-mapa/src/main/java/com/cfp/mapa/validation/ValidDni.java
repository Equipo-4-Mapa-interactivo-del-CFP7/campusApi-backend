package com.cfp.mapa.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidDniValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDni {

  String message() default "El DNI debe ser alfanumérico y tener entre 7 y 20 caracteres";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
