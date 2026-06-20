package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.validation.ValidDni;
import com.cfp.mapa.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record UsuarioOwnerRecoveryRequestDTO(

    @NotBlank
    @ValidDni
    String dni,

    @NotBlank
    @ValidPassword
    String recoveryPassword,

    @NotBlank
    @ValidPassword
    String nuevaPassword
) {

}
