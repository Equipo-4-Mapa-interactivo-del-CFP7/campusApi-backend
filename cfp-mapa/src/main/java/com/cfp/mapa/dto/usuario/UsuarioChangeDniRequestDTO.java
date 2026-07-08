package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.validation.ValidDni;
import jakarta.validation.constraints.NotBlank;

public record UsuarioChangeDniRequestDTO (

    @NotBlank(message = "El DNI es obligatorio")
    @ValidDni
    String dni
){

}
