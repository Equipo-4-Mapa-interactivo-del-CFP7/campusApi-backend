package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.validation.ValidApellido;
import com.cfp.mapa.validation.ValidNombre;
import jakarta.validation.constraints.NotBlank;

public record UsuarioChangeNombreApellidoRequestDTO (

    @NotBlank(message = "El nombre es obligatorio")
    @ValidNombre
    String nombre,

    @NotBlank(message = "El apellido es obligatorio")
    @ValidApellido
    String apellido
) {

}
