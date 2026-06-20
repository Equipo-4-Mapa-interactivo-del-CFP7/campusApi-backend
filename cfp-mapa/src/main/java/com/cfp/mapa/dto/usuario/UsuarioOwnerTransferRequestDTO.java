package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record UsuarioOwnerTransferRequestDTO (

    @NotBlank
    @ValidPassword
    String password
){

}
