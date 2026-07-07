package com.cfp.mapa.util;

import com.cfp.mapa.dto.usuario.UsuarioAutenticadoDTO;
import com.cfp.mapa.exception.AccionInvalidaException;
import com.cfp.mapa.exception.AccionNoPermitidaException;
import com.cfp.mapa.exception.UsuarioNotFoundException;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SecurityValidator {

  private final UsuarioRepository usuarioRepository;
  private final SecurityUtils securityUtils;

  /// @throws AccionNoPermitidaException HTTP 403 Forbidden + errorCode
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

  /// @throws UsuarioNotFoundException HTTP 404 Not Found
  public void validarNoEsUsuarioSystem(Long id, String dni) {

    if ("SYSTEM01".equals(dni)) {
      throw new UsuarioNotFoundException(id);
    }
  }

  /// @throws AccionInvalidaException HTTP 403 Forbidden
  public void validarNoEsNombreApellidoReservado(String nombre, String apellido) {

    if (nombre.equalsIgnoreCase("USUARIO") ||
        nombre.equalsIgnoreCase("SISTEMA")
    ) {
      throw new AccionInvalidaException("El nombre ingresado está reservado por el sistema");
    }

    if (apellido.equalsIgnoreCase("ELIMINADO") ||
        apellido.equalsIgnoreCase("PROCESO")
    ) {
      throw new AccionInvalidaException("El apellido ingresado está reservado por el sistema");
    }
  }

}