package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.validation.ValidDni;
import com.cfp.mapa.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record UsuarioLoginDTO(

    @NotBlank(message = "El DNI es obligatorio")
    @ValidDni
    String dni,

    @NotBlank(message = "La contraseña es obligatorio")
    @ValidPassword
    String password
) {

}
