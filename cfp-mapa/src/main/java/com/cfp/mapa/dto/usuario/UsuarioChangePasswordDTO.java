package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record UsuarioChangePasswordDTO(

    @NotBlank(message = "La contraseña actual es obligatoria")
    @ValidPassword
    String oldPassword,

    @NotBlank(message = "La contraseña nueva es obligatoria")
    @ValidPassword
    String newPassword
) {

}
