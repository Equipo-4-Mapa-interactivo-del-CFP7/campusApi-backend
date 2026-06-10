package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.Rol;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

  // UsuarioCreateRequestDTO -> Usuario
  public Usuario createToUsuario(UsuarioCreateRequestDTO request, String encodedPassword) {

    return Usuario.builder()
        .dni(request.dni())
        .password(encodedPassword)
        .rol(Rol.CHANGE_PASSWORD)
        .nombre(request.nombre())
        .apellido(request.apellido())
        .activo(true)
        .rolOriginal(Rol.PERSONAL)
        .build();
  }

  // Usuario -> UsuarioResponseDTO
  public UsuarioResponseDTO usuarioToResponse(Usuario usuario) {

    return new UsuarioResponseDTO(
        usuario.getId(),
        usuario.getDni(),
        usuario.getNombre(),
        usuario.getApellido(),
        usuario.isActivo()
    );
  }
}
