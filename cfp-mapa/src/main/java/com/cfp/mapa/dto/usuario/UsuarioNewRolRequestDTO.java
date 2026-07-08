package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.validation.ValidRol;
import jakarta.validation.constraints.NotBlank;

public record UsuarioNewRolRequestDTO(

    @NotBlank(message = "El rol es obligatorio")
    @ValidRol
    String rol
) {

}
