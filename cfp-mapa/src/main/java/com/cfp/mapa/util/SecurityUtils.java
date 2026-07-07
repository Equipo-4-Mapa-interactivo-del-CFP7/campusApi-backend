package com.cfp.mapa.util;

import com.cfp.mapa.dto.usuario.UsuarioAutenticadoDTO;
import com.cfp.mapa.exception.UsuarioNotFoundException;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.repository.UsuarioRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SecurityUtils {

  private final UsuarioRepository usuarioRepository;

  public UsuarioAutenticadoDTO getUsuarioLogueadoDto() {
    return (UsuarioAutenticadoDTO) Objects.requireNonNull(SecurityContextHolder
            .getContext()
            .getAuthentication())
        .getPrincipal();
  }

  /// @throws UsuarioNotFoundException HTTP 404 Not Found
  public Usuario usuarioLogueado() {

    Long usuarioId = getUsuarioLogueadoDto().id();

    Usuario usuarioLogueado = usuarioRepository.findById(usuarioId).orElseThrow(
        () -> new UsuarioNotFoundException(usuarioId)
    );

    return usuarioLogueado;
  }

}
