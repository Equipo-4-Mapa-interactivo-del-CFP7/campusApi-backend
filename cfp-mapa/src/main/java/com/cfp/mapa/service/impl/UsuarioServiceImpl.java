package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.usuario.UsuarioAutenticadoDTO;
import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.exception.AccionNoPermitidaException;
import com.cfp.mapa.exception.DniDuplicadoException;
import com.cfp.mapa.exception.DniNotFoundException;
import com.cfp.mapa.exception.PasswordIncorrectaException;
import com.cfp.mapa.mapper.UsuarioMapper;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.repository.UsuarioRepository;
import com.cfp.mapa.security.SecurityUtils;
import com.cfp.mapa.service.UsuarioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final UsuarioMapper usuarioMapper;
  private final PasswordEncoder passwordEncoder;
  private final SecurityUtils securityUtils;

  @Transactional
  @Override
  public UsuarioResponseDTO crearUsuario(UsuarioCreateRequestDTO request) {

    validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);

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
  public UsuarioResponseDTO restablecerPassword(String dni) {

    Usuario usuario = usuarioRepository.findByDni(dni).orElseThrow(
        () -> new DniNotFoundException(dni)
    );

    // Nadie puede restablecer la password de OWNER
    if (usuario.getRol().equals(Rol.OWNER)) {
      throw new AccionNoPermitidaException(
          "No se puede restablecer la contraseña del dueño del sistema."
      );
    }

    // OWNER puede restablecer a ADMIN
    if (usuario.getRol().equals(Rol.ADMIN)) {
      validarUsuarioActivoYRoles(Rol.OWNER);
    }

    // OWNER y ADMIN pueden restablecer a PERSONAL
    if (usuario.getRol().equals(Rol.PERSONAL)) {
      validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);
    }

    // Si el rol no es CHANGE_PASSWORD se restablece la password
    if (!usuario.getRol().equals(Rol.CHANGE_PASSWORD)) {

      usuario.setRolOriginal(usuario.getRol());
      usuario.setRol(Rol.CHANGE_PASSWORD);
      usuario.setPassword(dniToPasswordEncoded(usuario.getDni()));

      Usuario usuarioGuardado = usuarioRepository.save(usuario);

      return usuarioMapper.usuarioToResponse(usuarioGuardado);
    }

    // Si el rol es CHANGE_PASSWORD no se realiza ningun cambio
    validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);

    return usuarioMapper.usuarioToResponse(usuario);
  }

  @Transactional
  @Override
  public UsuarioResponseDTO cambiarEstadoActivoPorAdmin(String dni) {

    Usuario usuario = usuarioRepository.findByDni(dni).orElseThrow(
        () -> new DniNotFoundException(dni)
    );

    usuario.setActivo(!usuario.isActivo());
    Usuario usuarioGuardado = usuarioRepository.save(usuario);

//    TODO
//    if (!usuario.isActivo()) {
//      tokenBlacklistAsyncSafe(dni, "deactivated");
//    } else {
//      tokenBlacklistRemoveSafe(dni);
//    }

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

    if (usuario.getRol() == Rol.CHANGE_PASSWORD) {
      usuario.setRol(usuario.getRolOriginal());
      usuario.setRolOriginal(null);
    }

    usuario.setPassword(passwordEncoder.encode(newPassword));
    usuarioRepository.save(usuario);
//    TODO
//    tokenBlacklistAsyncSafe(dni, "password_changed");
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

//    TODO
//    tokenBlacklistAsyncSafe(dni, "rol_changed");

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

  // ======================================
  // FUNCIONES PRIVADAS
  // ======================================

  private String dniToPasswordEncoded(String dni) {
    return passwordEncoder.encode(dni);
  }

  private void validarUsuarioActivoYRoles(Rol... rolesPermitidos) {

    UsuarioAutenticadoDTO usuarioLogueado = securityUtils.getUsuarioLogueado();

    List<Rol> listaRolesPermitidos = List.of(rolesPermitidos);

    if (!usuarioRepository.existsByIdAndActivoTrueAndRolIn(
        usuarioLogueado.id(), listaRolesPermitidos)) {

      throw new AccionNoPermitidaException(
          "Su sesión ya no es válida. Sus permisos han cambiado o su cuenta fue desactivada."
      );
    }
  }

}
