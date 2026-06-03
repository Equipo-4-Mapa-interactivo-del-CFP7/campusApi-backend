package com.cfp.mapa.security.user;

import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Usuario usuario = usuarioRepository.findByDni(username)
        .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas"));

    return new UsuarioDetails(usuario);
  }
}
