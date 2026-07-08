package com.cfp.mapa.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidApellidoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidApellido {

  String message() default "El apellido debe tener entre 2 y 50 caracteres y solo puede contener letras, espacios, guiones o apóstrofes";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}