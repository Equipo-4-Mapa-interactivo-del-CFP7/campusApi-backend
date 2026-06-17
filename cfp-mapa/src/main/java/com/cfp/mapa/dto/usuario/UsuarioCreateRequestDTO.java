package com.cfp.mapa.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequestDTO(

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe contener exactamente 8 dígitos")
    String dni,

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(
        regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ '-]+$",
        message = "El nombre debe tener mínimo 2 caracteres y solo puede contener letras, espacios, guiones o apóstrofes"
    )
    String nombre,

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(
        regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ '-]+$",
        message = "El apellido debe tener mínimo 2 caracteres y solo puede contener letras, espacios, guiones o apóstrofes"
    )
    String apellido
) {

}
