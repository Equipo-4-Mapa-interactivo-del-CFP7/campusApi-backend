package com.cfp.mapa.security.user;

import com.cfp.mapa.model.Usuario;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UsuarioDetails implements UserDetails {

  @Getter
  private final Long id;
  @Getter
  private final String password;
  @Getter
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean activo;

  public UsuarioDetails(Usuario usuario) {

    this.id = usuario.getId();
    this.password = usuario.getPassword();
    this.activo = usuario.isActivo();

    if (usuario.getRol() != null) {

      this.authorities = Collections.singletonList(
          new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
      );
    } else {
      this.authorities = Collections.emptyList();
    }
  }

  @Override
  public String getUsername() {
    return String.valueOf(id);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;  }

  @Override
  public boolean isEnabled() {
    return activo;
  }
}
