package com.cfp.mapa.dto.usuario;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public record UsuarioAutenticadoDTO(

    Long id,
    Collection<? extends GrantedAuthority> authorities
) {

}
