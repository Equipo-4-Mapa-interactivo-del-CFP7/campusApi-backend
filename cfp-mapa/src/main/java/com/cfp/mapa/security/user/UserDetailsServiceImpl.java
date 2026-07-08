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

    // Si contiene "-" al comienzo es el dni del endpoint /login
    if (identificador.startsWith("-")) {

      String dniReal = identificador.substring(1);

      usuario = usuarioRepository.findByDni(dniReal)
          .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas"));
    } else {

      Long usuarioId = Long.parseLong(identificador);

      usuario = usuarioRepository.findById(usuarioId)
          .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas"));
    }

    return new UsuarioDetails(usuario);
  }
}
