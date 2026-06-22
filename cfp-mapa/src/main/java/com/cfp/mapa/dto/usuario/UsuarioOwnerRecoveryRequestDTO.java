package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.validation.ValidDni;
import com.cfp.mapa.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record UsuarioOwnerRecoveryRequestDTO(

    @NotBlank(message = "El DNI es obligatorio")
    @ValidDni
    String dni,

    @NotBlank(message = "La contraseña de recuperación es obligatoria")
    @ValidPassword
    String recoveryPassword,

    @NotBlank(message = "La nueva contraseña es obligatorio")
    @ValidPassword
    String nuevaPassword
) {

}
