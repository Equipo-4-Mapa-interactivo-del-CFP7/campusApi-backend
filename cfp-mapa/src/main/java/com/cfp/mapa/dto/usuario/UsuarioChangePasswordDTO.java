package com.cfp.mapa.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioChangePasswordDTO(

    @NotBlank(message = "La contraseña actual es obligatoria")
    @Size(min = 8, max = 60, message = "La contraseña debe tener al menos 8 caracteres")
    String oldPassword,

    @NotBlank(message = "La contraseña nueva es obligatoria")
    @Size(min = 8, max = 60, message = "La contraseña debe tener al menos 8 caracteres")
    String newPassword
) {

}
