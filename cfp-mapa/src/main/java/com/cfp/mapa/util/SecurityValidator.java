package com.cfp.mapa.util;

import com.cfp.mapa.dto.usuario.UsuarioAutenticadoDTO;
import com.cfp.mapa.exception.AccionNoPermitidaException;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.repository.UsuarioRepository;
import com.cfp.mapa.security.SecurityUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityValidator {

  private final UsuarioRepository usuarioRepository;
  private final SecurityUtils securityUtils;

  public void validarUsuarioActivoYRoles(Rol... rolesPermitidos) {
    UsuarioAutenticadoDTO usuarioLogueado = securityUtils.getUsuarioLogueadoDto();
    List<Rol> listaRolesPermitidos = List.of(rolesPermitidos);

    if (!usuarioRepository.existsByIdAndActivoTrueAndEliminadoFalseAndRolIn(
        usuarioLogueado.id(), listaRolesPermitidos)) {

      throw new AccionNoPermitidaException(
          "Su sesión ya no es válida. Sus permisos han cambiado o su cuenta fue desactivada."
      );
    }
  }
}