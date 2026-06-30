package com.cfp.mapa.service.impl;

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
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.repository.UsuarioRepository;
import com.cfp.mapa.security.SecurityUtils;
import com.cfp.mapa.service.AuditoriaService;
import com.cfp.mapa.service.UsuarioService;
import com.cfp.mapa.util.SecurityValidator;
import com.cfp.mapa.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final UsuarioMapper usuarioMapper;
  private final PasswordEncoder passwordEncoder;
  private final SecurityUtils securityUtils;
  private final AuditoriaService auditoriaService;
  private final SecurityValidator securityValidator;

  @Value("${app.ownerPasswordRecovery}")
  String emergencyPassword;

  @Transactional
  @Override
  public UsuarioResponseDTO crearUsuario(UsuarioCreateRequestDTO request) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);

    String rolRequest = request.rol().toUpperCase().trim();

    // No se pueden crear usuarios con rol OWNER / CHANGE_PASSWORD
    if (rolRequest.equals(Rol.OWNER.name()) || rolRequest.equals(Rol.CHANGE_PASSWORD.name())) {
      throw new RolInvalidoException("El rol proporcionado no es válido");
    }

    // ADMIN puede crear PERSONAL unicamente
    if (securityUtils.getUsuarioLogueadoDto().getRol() == Rol.ADMIN &&
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

    auditoriaService.registrarAccion(
        usuarioLogueado(),
        usuarioGuardado,
        TipoAccionAuditoria.USUARIO_CREADO
    );

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<UsuarioResponseDTO> listarUsuariosConFiltro(
      String dni,
      String nombre,
      String apellido,
      Boolean activo,
      String rol,
      Pageable pageable
  ) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);

    String dniParam = (dni != null && !dni.isBlank()) ?
        "%" + dni.toLowerCase().trim() + "%" : null;

    String nombreParam = (nombre != null && !nombre.isBlank()) ?
        "%" + nombre.toLowerCase().trim() + "%" : null;

    String apellidoParam = (apellido != null && !apellido.isBlank()) ?
        "%" + apellido.toLowerCase().trim() + "%" : null;

    Rol rolParam = (rol != null && !rol.isBlank()) ?
        Rol.valueOf(rol.toUpperCase().trim()) : null;

    Page<Usuario> usuariosPage = usuarioRepository.buscarUsuariosDinamico(
        dniParam, nombreParam, apellidoParam, activo, rolParam, pageable
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

    if (usuario.getRol().equals(Rol.CHANGE_PASSWORD)) {

      securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);

      throw new AccionInvalidaException(
          "El usuario ya tiene un restablecimiento de contraseña pendiente."
      );
    }

     usuario.setRolOriginal(usuario.getRol());
     usuario.setRol(Rol.CHANGE_PASSWORD);
     usuario.setPassword(dniToPasswordEncoded(usuario.getDni()));

     Usuario usuarioGuardado = usuarioRepository.save(usuario);

     auditoriaService.registrarAccion(
         usuarioLogueado(),
         usuarioGuardado,
         TipoAccionAuditoria.PASSWORD_RESTABLECIDA
     );

     return usuarioMapper.usuarioToResponse(usuarioGuardado);
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
      securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);
    }

    usuario.setActivo(!usuario.isActivo());
    Usuario usuarioGuardado = usuarioRepository.save(usuario);


    // Verificar si es el mismo usuario
    Usuario usuarioLogueado = usuarioLogueado();

    Usuario usuarioAfectado = usuarioLogueado.getId().equals(usuarioGuardado.getId()) ?
        null : usuarioGuardado;

    auditoriaService.registrarAccion(
        usuarioLogueado,
        usuarioAfectado,
        TipoAccionAuditoria.ESTADO_ACTIVO_MODIFICADO
    );

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  @Transactional
  @Override
  public void cambiarPassword(Long id, String oldPassword, String newPassword) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL, Rol.CHANGE_PASSWORD);

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    if (newPassword.equalsIgnoreCase(usuario.getDni()) ||
        newPassword.equalsIgnoreCase("cfp" + usuario.getDni())) {

      throw new PasswordIncorrectaException("No puedes usar esta contraseña");
    }

    if (!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
      throw new PasswordIncorrectaException();
    }

    // Si su rol era CHANGE_PASSWORD pasa a recuperar su rol real
    if (usuario.getRol() == Rol.CHANGE_PASSWORD) {
      usuario.setRol(usuario.getRolOriginal());
      usuario.setRolOriginal(null);
    }

    usuario.setPassword(passwordEncoder.encode(newPassword));
    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    // El usuario afectado siempre es si mismo
    auditoriaService.registrarAccion(
        usuarioLogueado(),
        null,
        TipoAccionAuditoria.PASSWORD_CAMBIADA
    );
  }

  @Transactional
  @Override
  public UsuarioResponseDTO cambiarRol(Long id, String newRol) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER);

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    if (usuario.getRol().equals(Rol.OWNER)) {
      throw new AccionInvalidaException("No se puede cambiar el rol del dueño del sistema");
    }

    if (usuario.getRol().equals(Rol.CHANGE_PASSWORD)) {
      throw new AccionInvalidaException("No se puede puede cambiar el rol 'CHANGE_PASSWORD'");
    }

    newRol = newRol.toUpperCase().trim();

    if (usuario.getRol().name().equals(newRol)) {
      throw new AccionInvalidaException("El usuario ya tiene asignado el rol " + newRol);
    }

    switch (newRol) {
      case "PERSONAL" -> usuario.setRol(Rol.PERSONAL);
      case "ADMIN" -> usuario.setRol(Rol.ADMIN);
      default -> throw new RolInvalidoException("El rol proporcionado no es válido");
    }

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    auditoriaService.registrarAccion(
        usuarioLogueado(),
        usuarioGuardado,
        TipoAccionAuditoria.ROL_MODIFICADO
    );

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  @Transactional(readOnly = true)
  @Override
  public UsuarioResponseDTO obtenerMiPerfil(Long id) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL, Rol.CHANGE_PASSWORD);

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

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);

    Rol rolLogueado = securityUtils.getUsuarioLogueadoDto().getRol();

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

  @Transactional
  @Override
  public void eliminarUsuario(Long id) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER);

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    if (usuario.getRol().equals(Rol.OWNER)) {
      throw new AccionInvalidaException("Un OWNER no puede eliminarse a sí mismo del sistema.");
    }

    // Ofuscar dni, nombre y apellido
    usuario.setDni("00000000");
    usuario.setNombre("USUARIO");
    usuario.setApellido("ELIMINADO");

    usuario.setActivo(false);
    usuario.setEliminado(true);

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    auditoriaService.registrarAccion(
        usuarioLogueado(),
        usuarioGuardado,
        TipoAccionAuditoria.USUARIO_ELIMINADO
    );

    // Ofuscar el usuario en toda la auditoria
    auditoriaService.anonimizarUsuario(
        usuarioGuardado.getId(),
        "USUARIO ELIMINADO",
        "00000000");
  }

  @Transactional
  @Override
  public void recuperarPasswordOwner(String dni, String recoveryPassword, String nuevaPassword) {

    Usuario usuario = usuarioRepository.findByDni(dni).orElseThrow(
        () -> new DniNotFoundException(dni)
    );

    if (!usuario.getRol().equals(Rol.OWNER)) {
      throw new AccionInvalidaException("No tienes permiso de realizar esta acción");
    }

    if (!passwordEncoder.matches(recoveryPassword, emergencyPassword)) {
      throw new AccionInvalidaException("Contraseña de recuperación incorrecta");
    }

    if (nuevaPassword.equalsIgnoreCase(usuario.getDni()) ||
        nuevaPassword.equalsIgnoreCase("cfp" + usuario.getDni())) {

      throw new PasswordIncorrectaException("No puedes usar esta contraseña");
    }

    // Si el dni pertenece a un OWNER y la recovery password es correcta
    String passwordEncoded = passwordEncoder.encode(nuevaPassword);
    usuario.setPassword(passwordEncoded);

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    // El usuario afectado siempre es si mismo
    auditoriaService.registrarAccion(
        usuarioGuardado,
        null,
        TipoAccionAuditoria.PASSWORD_OWNER_RECUPERADA
    );
  }

  @Transactional
  @Override
  public void transferirOwner(String password, Long id) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER);

    Usuario antiguoOwner = usuarioLogueado();

    // No se puede transferir a si mismo
    if (antiguoOwner.getId().equals(id)) {
      throw new AccionInvalidaException("No puedes transferirte el rol a ti mismo");
    }

    // Si la password no coincide, se lanza error con errorCode para desloguear
    if (!passwordEncoder.matches(password, antiguoOwner.getPassword())) {
      throw new AccionNoPermitidaException("No tienes permiso de realizar esta acción");
    }

    Usuario nuevoOwner = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    if (nuevoOwner.getRol().equals(Rol.CHANGE_PASSWORD)) {
      throw new AccionInvalidaException("El usuario debe tener rol válido");
    }

    // El OWNER pasa a ser ADMIN, el usuario seleccionado pasa a ser OWNER
    antiguoOwner.setRol(Rol.ADMIN);
    nuevoOwner.setRol(Rol.OWNER);

    usuarioRepository.save(antiguoOwner);
    usuarioRepository.save(nuevoOwner);

    auditoriaService.registrarAccion(
        antiguoOwner,
        nuevoOwner,
        TipoAccionAuditoria.OWNER_TRANSFERIDO
    );
  }

  @Transactional
  @Override
  public UsuarioResponseDTO cambiarDni(Long id, String nuevoDni) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL);

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    // El dni nuevo no puede ser el mismo que ya tiene el usuario
    if (usuario.getDni().equalsIgnoreCase(nuevoDni)) {
      throw new AccionInvalidaException("No puedes asignarle el mismo DNI que ya posee");
    }

    Usuario usuarioLogueado = usuarioLogueado();

    // Solo pueden editarse a si mismo o a un rol menor
    validarPermisosEdicion(usuario, usuarioLogueado);

    // Si supera los filtros es porque es su propio perfil o el de un rol permitido
    usuario.setDni(nuevoDni);

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    // Verificar si es el mismo usuario
    Usuario usuarioAfectado = usuarioLogueado.getId().equals(usuarioGuardado.getId()) ?
        null : usuarioGuardado;

    auditoriaService.registrarAccion(
        usuarioLogueado,
        usuarioAfectado,
        TipoAccionAuditoria.DNI_EDITADO
    );

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  @Transactional
  @Override
  public UsuarioResponseDTO cambiarNombreApellido(Long id, String nombre, String apellido) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL);

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    // Se normaliza el nombre y apellido para quitar espacios extra
    // y dejar la primera letra de cada uno en mayuscula
    nombre = StringUtils.normalizarNombre(nombre);
    apellido = StringUtils.normalizarNombre(apellido);

    // El nombre y apellido no pueden ser exactamente iguales a los que ya posee
    if (usuario.getNombre().equalsIgnoreCase(nombre) &&
        usuario.getApellido().equalsIgnoreCase(apellido)
    ) {

      throw new AccionInvalidaException("No puedes asignarle el mismo nombre y apellido que ya posee");
    }

    Usuario usuarioLogueado = usuarioLogueado();

    // Solo pueden editarse a si mismo o a un rol menor
    validarPermisosEdicion(usuario, usuarioLogueado);

    // Si supera los filtros es porque el nombre y apellido son validos
    usuario.setNombre(nombre);
    usuario.setApellido(apellido);

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    // Verificar si es el mismo usuario
    Usuario usuarioAfectado = usuarioLogueado.getId().equals(usuarioGuardado.getId()) ?
        null : usuarioGuardado;

    auditoriaService.registrarAccion(
        usuarioLogueado,
        usuarioAfectado,
        TipoAccionAuditoria.NOMBRE_APELLIDO_EDITADO
    );

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }

  // ======================================
  // FUNCIONES PRIVADAS
  // ======================================

  private String dniToPasswordEncoded(String dni) {

    // Se genera una password que es el prefijo "cfp" y el dni
    return passwordEncoder.encode("cfp" + dni);
  }

  private void validarJerarquias(Rol rolAfectado, String mensajeCasoOwner) {

    // Nadie puede modificar a OWNER
    if (rolAfectado.equals(Rol.OWNER)) {

      securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);

      throw new AccionInvalidaException(
          mensajeCasoOwner
      );
    }

    // OWNER puede modificar a ADMIN
    if (rolAfectado.equals(Rol.ADMIN)) {
      securityValidator.validarUsuarioActivoYRoles(Rol.OWNER);
    }

    // OWNER y ADMIN pueden modificar a PERSONAL
    if (rolAfectado.equals(Rol.PERSONAL)) {
      securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);
    }
  }

  private Usuario usuarioLogueado() {

    Long usuarioId = securityUtils.getUsuarioLogueadoDto().id();

    Usuario usuarioLogueado = usuarioRepository.findById(usuarioId).orElseThrow(
        () -> new UsuarioNotFoundException(usuarioId)
    );

    return usuarioLogueado;
  }

  private void validarPermisosEdicion(Usuario usuarioAfectado, Usuario usuarioLogueado) {

    Rol rolOperador = usuarioLogueado.getRol();
    Rol rolAfectado = usuarioAfectado.getRol();

    boolean esSiMismo = usuarioLogueado.getId().equals(usuarioAfectado.getId());

    // Solo pueden editarse a si mismos o un rol menor
    // Filtros en caso que intenten editar a otro usuario
    if (!esSiMismo) {

      // Nadie puede modificar a CHANGE_PASSWORD
      if (rolAfectado.equals(Rol.CHANGE_PASSWORD)) {
        throw new AccionInvalidaException("No puedes modificar a un usuario con rol pendiente");
      }

      // OWNER es unico y puede editar a cualquiera
      if (rolOperador.equals(Rol.OWNER)) {
        if (rolAfectado.equals(Rol.OWNER)) {
          throw new AccionInvalidaException("No puedes modificar a otro dueño del sistema");
        }
      }
      // ADMIN solo puede editar a personal
      else if (rolOperador.equals(Rol.ADMIN)) {
        if (!rolAfectado.equals(Rol.PERSONAL)) {
          throw new AccionInvalidaException("No tienes permisos para modificar a este usuario");
        }
      }
      // PERSONAL no puede editar a otros usuarios
      else if (rolOperador.equals(Rol.PERSONAL)) {
        throw new AccionInvalidaException("No tienes permitido modificar perfiles ajenos");
      }
    }
  }
}
