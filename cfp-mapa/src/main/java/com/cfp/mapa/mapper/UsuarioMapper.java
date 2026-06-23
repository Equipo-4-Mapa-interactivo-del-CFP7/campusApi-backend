package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

  // UsuarioCreateRequestDTO -> Usuario
  public Usuario createToUsuario(UsuarioCreateRequestDTO request, String encodedPassword) {

    String nombreNormalizado = StringUtils.normalizarNombre(request.nombre());
    String apellidoNormalizado = StringUtils.normalizarNombre(request.apellido());


    Rol rol = Rol.valueOf(request.rol().toUpperCase());

    return Usuario.builder()
        .dni(request.dni())
        .password(encodedPassword)
        .rol(Rol.CHANGE_PASSWORD)
        .nombre(nombreNormalizado)
        .apellido(apellidoNormalizado)
        .activo(true)
        .eliminado(false)
        .rolOriginal(rol)
        .build();
  }

  // Usuario -> UsuarioResponseDTO
  public UsuarioResponseDTO usuarioToResponse(Usuario usuario) {

    return new UsuarioResponseDTO(
        usuario.getId(),
        usuario.getDni(),
        usuario.getRol(),
        usuario.getNombre(),
        usuario.getApellido(),
        usuario.isActivo()
    );
  }

}
