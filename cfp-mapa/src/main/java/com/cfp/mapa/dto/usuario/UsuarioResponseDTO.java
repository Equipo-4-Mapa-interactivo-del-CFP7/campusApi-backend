package com.cfp.mapa.dto.usuario;

public record UsuarioResponseDTO(

    Long id,
    String dni,
    String nombre,
    String apellido,
    Boolean activo
)
{

}
