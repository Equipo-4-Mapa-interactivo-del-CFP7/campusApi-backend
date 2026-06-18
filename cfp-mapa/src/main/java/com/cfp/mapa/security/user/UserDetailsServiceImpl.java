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
  public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {

    Usuario usuario;

    try {
      Long usuarioId = Long.parseLong(identificador);

      usuario = usuarioRepository.findById(usuarioId)
          .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas"));

    } catch (NumberFormatException e) {
      usuario = usuarioRepository.findByDni(identificador)
          .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas"));
    }

    return new UsuarioDetails(usuario);
  }
}
