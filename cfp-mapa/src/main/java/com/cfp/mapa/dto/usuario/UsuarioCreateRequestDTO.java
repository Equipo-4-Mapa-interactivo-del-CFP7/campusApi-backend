package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.validation.ValidApellido;
import com.cfp.mapa.validation.ValidDni;
import com.cfp.mapa.validation.ValidNombre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequestDTO(

    @NotBlank(message = "El DNI es obligatorio")
    @ValidDni
    String dni,

    @NotBlank(message = "El nombre es obligatorio")
    @ValidNombre
    String nombre,

    @NotBlank(message = "El apellido es obligatorio")
    @ValidApellido
    String apellido
) {

}
