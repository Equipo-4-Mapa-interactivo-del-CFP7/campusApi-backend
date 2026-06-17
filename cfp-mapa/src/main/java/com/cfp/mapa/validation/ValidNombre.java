package com.cfp.mapa.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidNombreValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNombre {

  String message() default "El nombre debe tener entre 2 y 50 caracteres y solo puede contener letras, espacios, guiones o apóstrofes";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}