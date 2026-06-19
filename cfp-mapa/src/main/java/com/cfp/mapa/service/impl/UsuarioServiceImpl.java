package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.usuario.UsuarioAutenticadoDTO;
import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.exception.AccionInvalidaException;
import com.cfp.mapa.exception.AccionNoPermitidaException;
import com.cfp.mapa.exception.DniDuplicadoException;
import com.cfp.mapa.exception.DniNotFoundException;
import com.cfp.mapa.exception.PasswordIncorrectaException;
import com.cfp.mapa.exception.RolInvalidoException;
import com.cfp.mapa.exception.UsuarioNotFoundException;
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

    String rolRequest = request.rol().toUpperCase().trim();

    // No se pueden crear usuarios con rol OWNER / CHANGE_PASSWORD
    if (rolRequest.equals(Rol.OWNER.name()) || rolRequest.equals(Rol.CHANGE_PASSWORD.name())) {
      throw new RolInvalidoException("El rol proporcionado no es válido");
    }

    // ADMIN puede crear PERSONAL unicamente
    if (securityUtils.getUsuarioLogueado().getRol() == Rol.ADMIN &&
        rolRequest.equals(Rol.ADMIN.name())) {

      throw new AccionInvalidaException(String.format("Un %s solo puede crear %s",
          Rol.ADMIN.name(), Rol.PERSONAL.name()));
    }

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

    validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);

    String dniParam = (dni != null && !dni.isBlank()) ?
        "%" + dni.trim() + "%" : null;

    String nombreParam = (nombre != null && !nombre.isBlank()) ?
        "%" + nombre.toLowerCase().trim() + "%" : null;

    String apellidoParam = (apellido != null && !apellido.isBlank()) ?
        "%" + apellido.toLowerCase().trim() + "%" : null;

    Page<Usuario> usuariosPage = usuarioRepository.buscarUsuariosDinamico(
        dniParam, nombreParam, apellidoParam, activo, pageable
    );

    return usuariosPage.map(usuarioMapper::usuarioToResponse);
  }

  @Transactional
  @Override
  public UsuarioResponseDTO restablecerPassword(Long id) {

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    validarJerarquias(
        usuario.getRol(),
        "No se puede restablecer la contraseña del dueño del sistema."
    );

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
  public UsuarioResponseDTO cambiarEstadoActivo(Long id) {

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    validarJerarquias(
        usuario.getRol(),
        "No se puede cambiar el estado del dueño del sistema"
    );

    // OWNER y ADMIN pueden modificar a alguien que deba cambiar su clave
    if (usuario.getRol().equals(Rol.CHANGE_PASSWORD)) {
      validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);
    }

    usuario.setActivo(!usuario.isActivo());
    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  @Transactional
  @Override
  public void cambiarPassword(Long id, String oldPassword, String newPassword) {

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    if (!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
      throw new PasswordIncorrectaException();
    }

    // Si su rol era CHANGE_PASSWORD pasa a recuperar su rol real
    if (usuario.getRol() == Rol.CHANGE_PASSWORD) {
      usuario.setRol(usuario.getRolOriginal());
      usuario.setRolOriginal(null);
    }

    usuario.setPassword(passwordEncoder.encode(newPassword));
    usuarioRepository.save(usuario);
  }

  @Transactional
  @Override
  public UsuarioResponseDTO cambiarRol(Long id, String newRol) {

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    validarJerarquias(
        usuario.getRol(),
        "No se puede cambiar el rol del dueño del sistema"
    );

    if (usuario.getRol().equals(Rol.CHANGE_PASSWORD)) {
      throw new AccionInvalidaException("No se puede puede cambiar el rol 'CHANGE_PASSWORD'");
    }

    newRol = newRol.toUpperCase().trim();

    switch (newRol) {
      case "PERSONAL" -> usuario.setRol(Rol.PERSONAL);
      case "ADMIN" -> usuario.setRol(Rol.ADMIN);
      default -> throw new RolInvalidoException("El rol proporcionado no es válido");
    }

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  @Transactional(readOnly = true)
  @Override
  public UsuarioResponseDTO obtenerMiPerfil(Long id) {

    validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL, Rol.CHANGE_PASSWORD);

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    if (usuario.getRol().equals(Rol.CHANGE_PASSWORD)) {
      throw new AccionInvalidaException("Debes cambiar tu contraseña para acceder a tu perfil");
    }

    return usuarioMapper.usuarioToResponse(usuario);
  }

  @Transactional(readOnly = true)
  @Override
  public UsuarioResponseDTO obtenerPerfil(Long id) {

    validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);

    Rol rolLogueado = securityUtils.getUsuarioLogueado().getRol();

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    // El rol OWNER solo puede ser visto por OWNER
    if (usuario.getRol().equals(Rol.OWNER)) {

      if (rolLogueado.equals(Rol.ADMIN)) {
        throw new AccionInvalidaException(
            "No tienes permitido ver el perfil de este usuario"
        );
      }
    }

    return usuarioMapper.usuarioToResponse(usuario);
  }

  // ======================================
  // FUNCIONES PRIVADAS
  // ======================================

  private String dniToPasswordEncoded(String dni) {

    // Se genera una password que es el prefijo "cfp" y el dni
    return passwordEncoder.encode("cfp" + dni);
  }

  private void validarUsuarioActivoYRoles(Rol... rolesPermitidos) {

    UsuarioAutenticadoDTO usuarioLogueado = securityUtils.getUsuarioLogueado();

    List<Rol> listaRolesPermitidos = List.of(rolesPermitidos);

    if (!usuarioRepository.existsByIdAndActivoTrueAndEliminadoFalseAndRolIn(
        usuarioLogueado.id(), listaRolesPermitidos)) {

      throw new AccionNoPermitidaException(
          "Su sesión ya no es válida. Sus permisos han cambiado o su cuenta fue desactivada."
      );
    }
  }

  private void validarJerarquias(Rol rolAfectado, String mensajeCasoOwner) {

    // Nadie puede modificar a OWNER
    if (rolAfectado.equals(Rol.OWNER)) {

      validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);

      throw new AccionInvalidaException(
          mensajeCasoOwner
      );
    }

    // OWNER puede modificar a ADMIN
    if (rolAfectado.equals(Rol.ADMIN)) {
      validarUsuarioActivoYRoles(Rol.OWNER);
    }

    // OWNER y ADMIN pueden modificar a PERSONAL
    if (rolAfectado.equals(Rol.PERSONAL)) {
      validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);
    }
  }

}
