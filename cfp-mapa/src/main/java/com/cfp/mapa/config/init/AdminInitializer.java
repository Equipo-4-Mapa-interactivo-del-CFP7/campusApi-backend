package com.cfp.mapa.config.init;

import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// TODO: Eliminar antes de subirlo a produccion
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    if (!usuarioRepository.existsByDni("123")) {
      Usuario admin = Usuario.builder()
          .dni("123")
          .nombre("Administrador")
          .apellido("Del Sistema")
          .password(passwordEncoder.encode("admin123"))
          .rol(Rol.ADMIN)
          .activo(true)
          .cambiarPassword(false)
          .build();

      usuarioRepository.save(admin);
    }

    if (!usuarioRepository.existsByDni("456")) {
      Usuario personal = Usuario.builder()
          .dni("456")
          .nombre("Personal")
          .apellido("Institucional")
          .password(passwordEncoder.encode("personal456"))
          .rol(Rol.PERSONAL)
          .activo(true)
          .cambiarPassword(false)
          .build();

      usuarioRepository.save(personal);
    }
  }
}
