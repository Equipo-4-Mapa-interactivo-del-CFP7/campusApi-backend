package com.cfp.mapa.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioChangePasswordDTO (

    @Size(min = 8, max = 60)
    @NotBlank
    String oldPassword,

    @Size(min = 8, max = 60)
    @NotBlank
    String newPassword
) {

}
