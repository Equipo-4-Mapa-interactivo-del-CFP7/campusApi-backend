package com.cfp.mapa.security;

import com.cfp.mapa.dto.usuario.UsuarioAutenticadoDTO;
import java.util.Objects;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

  public UsuarioAutenticadoDTO getUsuarioLogueado() {
    return (UsuarioAutenticadoDTO) Objects.requireNonNull(SecurityContextHolder
            .getContext()
            .getAuthentication())
        .getPrincipal();
  }
}
