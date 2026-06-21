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

    String nombreNormalizado = normalizarNombre(request.nombre());
    String apellidoNormalizado = normalizarNombre(request.apellido());

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

  // ======================================
  // FUNCIONES PRIVADAS
  // ======================================

  private String normalizarNombre(String texto) {

    if (texto == null || texto.isBlank()) {
      return texto;
    }

    String textoLimpio = texto.trim().replaceAll("\\s+", " ");

    String[] palabras = textoLimpio.split(" ");
    StringBuilder resultado = new StringBuilder();

    for (String palabra : palabras) {
      if (!palabra.isEmpty()) {
        resultado.append(Character.toUpperCase(palabra.charAt(0)))
            .append(palabra.substring(1).toLowerCase())
            .append(" ");
      }
    }

    return resultado.toString().trim();
  }
}
