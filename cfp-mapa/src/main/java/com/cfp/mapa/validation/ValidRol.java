package com.cfp.mapa.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidRolValidator.class)
public @interface ValidRol {

  String message() default "El rol proporcionado no es válido";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}