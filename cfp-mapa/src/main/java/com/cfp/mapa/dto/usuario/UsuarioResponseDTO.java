package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.model.enums.Rol;

public record UsuarioResponseDTO(

    Long id,
    String dni,
    Rol rol,
    String nombre,
    String apellido,
    Boolean activo
)
{

}
