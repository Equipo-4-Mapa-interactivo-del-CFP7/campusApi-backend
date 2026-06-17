package com.cfp.mapa.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioLoginDTO(

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe contener exactamente 8 dígitos")
    String dni,

    @NotBlank(message = "La contraseña es obligatorio")
    @Size(min = 8, max = 60, message = "La contraseña debe tener al menos 8 caracteres")
    String password
) {

}
