package com.cfp.mapa.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioLoginDTO(

    @Size(max = 15)
    @NotBlank
    String dni,

    @Size(min = 8, max = 60)
    @NotBlank
    String password
) {

}
