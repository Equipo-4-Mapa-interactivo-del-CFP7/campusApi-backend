package com.cfp.mapa.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequestDTO(

    @Size(max = 15)
    @NotBlank
    String dni,

    @Size(max = 100)
    @NotBlank
    String nombre,

    @Size(max = 100)
    @NotBlank
    String apellido
) {

}
