package com.cfp.mapa.dto.usuario;

import com.cfp.mapa.model.enums.Rol;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public record UsuarioAutenticadoDTO(

    Long id,
    Collection<? extends GrantedAuthority> authorities
) {

  public Rol getRol() {
    return this.authorities().stream()
        .map(org.springframework.security.core.GrantedAuthority::getAuthority)
        .map(auth -> auth.replace("ROLE_", ""))
        .map(Rol::valueOf)
        .findFirst()
        .orElseThrow(() ->
            new IllegalStateException("Usuario sin rol asignado en el contexto de seguridad"));
  }
}
