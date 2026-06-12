package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.exception.DniDuplicadoException;
import com.cfp.mapa.exception.DniNotFoundException;
import com.cfp.mapa.exception.PasswordIncorrectaException;
import com.cfp.mapa.mapper.UsuarioMapper;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.repository.UsuarioRepository;
import com.cfp.mapa.service.UsuarioService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final UsuarioMapper usuarioMapper;
  private final PasswordEncoder passwordEncoder;
  private final StringRedisTemplate redisTemplate;

  @Value("${app.jwt.expiration-ms}")
  private Long jwtExpirationMs;

  @Transactional
  @Override
  public UsuarioResponseDTO crearUsuarioPorAdmin(UsuarioCreateRequestDTO request) {

    if (usuarioRepository.existsByDni(request.dni())) {
      throw new DniDuplicadoException(request.dni());
    }

    String encodedPassword = dniToPasswordEncoded(request.dni());

    Usuario usuarioGuardado = usuarioMapper.createToUsuario(request, encodedPassword);
    usuarioRepository.save(usuarioGuardado);

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<UsuarioResponseDTO> listarUsuariosConFiltro(
      String dni,
      String nombre,
      String apellido,
      Boolean activo,
      Pageable pageable
  ) {

    Page<Usuario> usuariosPage = usuarioRepository.buscarUsuariosDinamico(
        dni, nombre, apellido, activo, pageable
    );

    return usuariosPage.map(usuarioMapper::usuarioToResponse);
  }

  @Transactional
  @Override
  public UsuarioResponseDTO restablecerPasswordPorAdmin(String dni) {

    Usuario usuario = usuarioRepository.findByDni(dni).orElseThrow(
        () -> new DniNotFoundException(dni)
    );

    usuario.setRolOriginal(usuario.getRol());
    usuario.setRol(Rol.CHANGE_PASSWORD);
    usuario.setPassword(dniToPasswordEncoded(usuario.getDni()));

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    String key = "blacklist:" + dni;
    redisTemplate.opsForValue().set(key, "revoked", Duration.ofMillis(jwtExpirationMs));

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  @Transactional
  @Override
  public UsuarioResponseDTO cambiarEstadoActivoPorAdmin(String dni) {

    Usuario usuario = usuarioRepository.findByDni(dni).orElseThrow(
        () -> new DniNotFoundException(dni)
    );

    usuario.setActivo(!usuario.isActivo());
    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    String key = "blacklist:" + dni;

    if (!usuario.isActivo()) {
      redisTemplate.opsForValue().set(key, "deactivated", Duration.ofMillis(jwtExpirationMs));
    } else {
      redisTemplate.delete(key);
    }

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  @Transactional
  @Override
  public void cambiarPassword(String dni, String oldPassword, String newPassword) {

    Usuario usuario = usuarioRepository.findByDni(dni).orElseThrow(
        () -> new DniNotFoundException(dni)
    );

    if (!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
      throw new PasswordIncorrectaException();
    }

    usuario.setPassword(passwordEncoder.encode(newPassword));
    usuarioRepository.save(usuario);

    String key = "blacklist:" + dni;
    redisTemplate.opsForValue().set(key, "password_changed", Duration.ofMillis(jwtExpirationMs));
  }

  @Transactional
  @Override
  public UsuarioResponseDTO cambiarRolPorAdmin(String dni) {

    Usuario usuario = usuarioRepository.findByDni(dni).orElseThrow(
        () -> new DniNotFoundException(dni)
    );

    usuario.setRol(
        usuario.getRol() == Rol.ADMIN ? Rol.PERSONAL : Rol.ADMIN
    );

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    String key = "blacklist:" + dni;
    redisTemplate.opsForValue().set(key, "rol_changed", Duration.ofMillis(jwtExpirationMs));

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  @Transactional(readOnly = true)
  @Override
  public UsuarioResponseDTO obtenerMiPerfil(String dni) {

    Usuario usuario = usuarioRepository.findByDni(dni).orElseThrow(
        () -> new DniNotFoundException(dni)
    );

    return usuarioMapper.usuarioToResponse(usuario);
  }

  @Transactional(readOnly = true)
  @Override
  public UsuarioResponseDTO obtenerPerfilPorAdmin(String dni) {

    Usuario usuario = usuarioRepository.findByDni(dni).orElseThrow(
        () -> new DniNotFoundException(dni)
    );

    return usuarioMapper.usuarioToResponse(usuario);
  }

  // ---------- FUNCIONES PRIVADAS
  private String dniToPasswordEncoded(String dni) {
    return passwordEncoder.encode(dni);
  }
}
