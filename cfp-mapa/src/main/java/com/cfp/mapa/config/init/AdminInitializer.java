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
    if (!usuarioRepository.existsByDni("11112222")) {
      Usuario admin = Usuario.builder()
          .dni("11112222")
          .nombre("Dueño")
          .apellido("Total")
          .password(passwordEncoder.encode("owner123"))
          .rol(Rol.OWNER)
          .activo(true)
          .rolOriginal(null)
          .build();

      usuarioRepository.save(admin);
    }

    if (!usuarioRepository.existsByDni("12345678")) {
      Usuario admin = Usuario.builder()
          .dni("12345678")
          .nombre("Administrador")
          .apellido("Del Sistema")
          .password(passwordEncoder.encode("administrador"))
          .rol(Rol.ADMIN)
          .activo(true)
          .rolOriginal(null)
          .build();

      usuarioRepository.save(admin);
    }

    if (!usuarioRepository.existsByDni("23456789")) {
      Usuario personal = Usuario.builder()
          .dni("23456789")
          .nombre("Personal")
          .apellido("Institucional")
          .password(passwordEncoder.encode("personal"))
          .rol(Rol.PERSONAL)
          .activo(true)
          .rolOriginal(null)
          .build();

      usuarioRepository.save(personal);
    }
  }
}
