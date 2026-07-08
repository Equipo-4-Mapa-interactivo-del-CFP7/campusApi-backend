package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.auditoria.AuditoriaUsuariosDetallesDTO;
import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.exception.AccionInvalidaException;
import com.cfp.mapa.exception.AccionNoPermitidaException;
import com.cfp.mapa.exception.DniDuplicadoException;
import com.cfp.mapa.exception.DniNotFoundException;
import com.cfp.mapa.exception.OperacionInvalidaException;
import com.cfp.mapa.exception.PasswordIncorrectaException;
import com.cfp.mapa.exception.RolInvalidoException;
import com.cfp.mapa.exception.UsuarioNotFoundException;
import com.cfp.mapa.mapper.UsuarioMapper;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.repository.UsuarioRepository;
import com.cfp.mapa.util.SecurityUtils;
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

    // No se pueden crear usuarios con rol OWNER / CHANGE_PASSWORD / SYSTEM
    if (rolRequest.equals(Rol.OWNER.name()) ||
        rolRequest.equals(Rol.CHANGE_PASSWORD.name()) ||
        rolRequest.equals(Rol.SYSTEM.name())
    ) {

      throw new RolInvalidoException("El rol proporcionado no es válido");
    }

    // ADMIN puede crear PERSONAL unicamente
    if (securityUtils.getUsuarioLogueadoDto().getRol() == Rol.ADMIN &&
        rolRequest.equals(Rol.ADMIN.name())) {

      throw new AccionInvalidaException(String.format("Un %s solo puede crear %s",
          Rol.ADMIN.name(), Rol.PERSONAL.name()));
    }

    String dniNormalizado = StringUtils.normalizarDni(request.dni());

    if (usuarioRepository.existsByDni(dniNormalizado)) {
      throw new DniDuplicadoException(request.dni());
    }

    securityValidator.validarNoEsNombreApellidoReservado(request.nombre(), request.apellido());

    // Si supera todos los filtros, se crea el usuario
    String encodedPassword = dniToPasswordEncoded(dniNormalizado);

    Usuario usuarioGuardado = usuarioMapper.createToUsuario(request, dniNormalizado, encodedPassword);
    usuarioRepository.save(usuarioGuardado);

    auditoriaService.registrarAccion(
        securityUtils.usuarioLogueado(),
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

    asegurarUsuarioNoEliminado(usuario);

    securityValidator.validarNoEsUsuarioSystem(usuario.getId(), usuario.getDni());

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
         securityUtils.usuarioLogueado(),
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

    asegurarUsuarioNoEliminado(usuario);

    securityValidator.validarNoEsUsuarioSystem(usuario.getId(), usuario.getDni());

    validarJerarquias(
        usuario.getRol(),
        "No se puede cambiar el estado del dueño del sistema"
    );

    // OWNER y ADMIN pueden modificar a alguien que deba cambiar su clave
    if (usuario.getRol().equals(Rol.CHANGE_PASSWORD)) {
      securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN);
    }

    // Crear detalles de la auditoria
    Long usuarioAfectadoId = usuario.getId();
    boolean activoAnterior = usuario.isActivo();
    boolean activoNuevo = !usuario.isActivo();

    AuditoriaUsuariosDetallesDTO detallesDTO = AuditoriaUsuariosDetallesDTO.builder()
        .usuarioAfectadoId(usuarioAfectadoId)
        .activoAnterior(activoAnterior)
        .activoNuevo(activoNuevo)
        .build();

    usuario.setActivo(!usuario.isActivo());
    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    // Verificar si es el mismo usuario
    Usuario usuarioLogueado = securityUtils.usuarioLogueado();

    Usuario usuarioAfectado = usuarioGuardado.getId().equals(usuarioLogueado.getId()) ?
        null : usuarioGuardado;

    auditoriaService.registrarAccion(
        usuarioLogueado,
        usuarioAfectado,
        null,
        TipoAccionAuditoria.ESTADO_ACTIVO_MODIFICADO,
        detallesDTO
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

    String dni = usuario.getDni();
    String dniSinCero = dni.startsWith("0") ? dni.substring(1) : dni;

    // La nueva password no puede ser el dni o cfp+dni
    // Se verifican los casos especiales de dni de 7 caracteres
    if (newPassword.equalsIgnoreCase(dni) ||
        newPassword.equalsIgnoreCase(dniSinCero) ||
        newPassword.equalsIgnoreCase("cfp" + dni) ||
        newPassword.equalsIgnoreCase("cfp" + dniSinCero)) {

      throw new PasswordIncorrectaException("No puedes usar esta contraseña");
    }

    // Verificar que la password actual ingresada sea correcta
    // En caso de ser la default cfp+dni del rol CHANGE_PASSWORD se hace una revision con las
    // variantes del dni con y sin 0 (cero) al comienzo
    if (usuario.getRol().equals(Rol.CHANGE_PASSWORD)) {

      boolean matchesConCero = passwordEncoder.matches("cfp" + dni, usuario.getPassword());
      boolean matchesSinCero = passwordEncoder.matches("cfp" + dniSinCero, usuario.getPassword());

      if (!oldPassword.equals("cfp" + dni) && !oldPassword.equals("cfp" + dniSinCero)) {
        throw new PasswordIncorrectaException();
      }

      if (!matchesConCero && !matchesSinCero) {
        throw new PasswordIncorrectaException();
      }
    } else {
      if (!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
        throw new PasswordIncorrectaException();
      }
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
        securityUtils.usuarioLogueado(),
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

    asegurarUsuarioNoEliminado(usuario);

    securityValidator.validarNoEsUsuarioSystem(usuario.getId(), usuario.getDni());

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

    Rol rolAnterior = usuario.getRol();

    switch (newRol) {
      case "PERSONAL" -> usuario.setRol(Rol.PERSONAL);
      case "ADMIN" -> usuario.setRol(Rol.ADMIN);
      default -> throw new RolInvalidoException("El rol proporcionado no es válido");
    }

    Rol rolNuevo = usuario.getRol();

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    AuditoriaUsuariosDetallesDTO detallesDTO = AuditoriaUsuariosDetallesDTO.builder()
        .usuarioAfectadoId(usuarioGuardado.getId())
        .rolAnterior(rolAnterior)
        .rolNuevo(rolNuevo)
        .build();

    auditoriaService.registrarAccion(
        securityUtils.usuarioLogueado(),
        usuarioGuardado,
        null,
        TipoAccionAuditoria.ROL_MODIFICADO,
        detallesDTO
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

    securityValidator.validarNoEsUsuarioSystem(usuario.getId(), usuario.getDni());

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

    asegurarUsuarioNoEliminado(usuario);

    securityValidator.validarNoEsUsuarioSystem(usuario.getId(), usuario.getDni());

    if (usuario.getRol().equals(Rol.OWNER)) {
      throw new AccionInvalidaException("Un OWNER no puede eliminarse a sí mismo del sistema.");
    }

    String dniOfuscado = "ANON-" + usuario.getId();
    String nombreOfuscado = "USUARIO";
    String apellidoOfuscado = "ELIMINADO";

    // Ofuscar dni, nombre y apellido
    usuario.setDni(dniOfuscado);
    usuario.setNombre(nombreOfuscado);
    usuario.setApellido(apellidoOfuscado);

    usuario.setActivo(false);
    usuario.setEliminado(true);

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    auditoriaService.registrarAccion(
        securityUtils.usuarioLogueado(),
        usuarioGuardado,
        TipoAccionAuditoria.USUARIO_ELIMINADO
    );

    String nombreApellidoOfuscado = String.format("%s %s", nombreOfuscado, apellidoOfuscado);

    // Ofuscar el usuario en toda la auditoria
    auditoriaService.anonimizarUsuario(
        usuarioGuardado.getId(),
        nombreApellidoOfuscado,
        dniOfuscado);
  }

  @Transactional
  @Override
  public void recuperarPasswordOwner(String dni, String recoveryPassword, String nuevaPassword) {

    String dniNormalizado = StringUtils.normalizarDni(dni);

    Usuario usuario = usuarioRepository.findByDni(dniNormalizado).orElseThrow(
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

    Usuario antiguoOwner = securityUtils.usuarioLogueado();

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

    asegurarUsuarioNoEliminado(nuevoOwner);
    asegurarUsuarioActivo(nuevoOwner);

    securityValidator.validarNoEsUsuarioSystem(nuevoOwner.getId(), nuevoOwner.getDni());

    if (nuevoOwner.getRol().equals(Rol.CHANGE_PASSWORD)) {
      throw new AccionInvalidaException("El usuario debe tener rol válido");
    }

    // Crear JSON de auditoria
    Long usuarioAfectadoId = nuevoOwner.getId();
    Rol rolAnterior = nuevoOwner.getRol();
    Rol rolNuevo = Rol.OWNER;

    AuditoriaUsuariosDetallesDTO detallesDTO = AuditoriaUsuariosDetallesDTO.builder()
        .usuarioAfectadoId(usuarioAfectadoId)
        .rolAnterior(rolAnterior)
        .rolNuevo(rolNuevo)
        .build();

    // El OWNER pasa a ser ADMIN, el usuario seleccionado pasa a ser OWNER
    antiguoOwner.setRol(Rol.ADMIN);
    nuevoOwner.setRol(Rol.OWNER);

    usuarioRepository.save(antiguoOwner);
    usuarioRepository.save(nuevoOwner);

    auditoriaService.registrarAccion(
        antiguoOwner,
        nuevoOwner,
        null,
        TipoAccionAuditoria.OWNER_TRANSFERIDO,
        detallesDTO
    );
  }

  @Transactional
  @Override
  public UsuarioResponseDTO cambiarDni(Long id, String nuevoDni) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL);

    Usuario usuario = usuarioRepository.findById(id).orElseThrow(
        () -> new UsuarioNotFoundException(id)
    );

    asegurarUsuarioNoEliminado(usuario);

    securityValidator.validarNoEsUsuarioSystem(usuario.getId(), usuario.getDni());

    nuevoDni = StringUtils.normalizarDni(nuevoDni);

    // El dni nuevo no puede ser el mismo que ya tiene el usuario
    if (usuario.getDni().equalsIgnoreCase(nuevoDni)) {
      throw new AccionInvalidaException("No puedes asignarle el mismo DNI que ya posee");
    }

    // El dni nuevo no debe estar registrado
    if (usuarioRepository.existsByDni(nuevoDni)) {
      throw new AccionInvalidaException("No puedes asignar un dni ya registrado");
    }

    Usuario usuarioLogueado = securityUtils.usuarioLogueado();

    // Solo pueden editarse a si mismo o a un rol menor
    validarPermisosEdicion(usuario, usuarioLogueado);

    // Si supera los filtros es porque es su propio perfil o el de un rol permitido

    // Variables para los detalles en auditoria
    Long usuarioAfectadoId = usuario.getId();
    String dniAnterior = usuario.getDni();
    String dniNuevo = nuevoDni;

    AuditoriaUsuariosDetallesDTO detallesDTO = AuditoriaUsuariosDetallesDTO.builder()
        .usuarioAfectadoId(usuarioAfectadoId)
        .dniAnterior(dniAnterior)
        .dniNuevo(dniNuevo)
        .build();

    usuario.setDni(nuevoDni);

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    // Verificar si es el mismo usuario
    Usuario usuarioAfectado = usuarioGuardado.getId().equals(usuarioLogueado.getId())
        ? null
        : usuarioGuardado;

    auditoriaService.registrarAccion(
        usuarioLogueado,
        usuarioAfectado,
        null,
        TipoAccionAuditoria.DNI_EDITADO,
        detallesDTO
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

    asegurarUsuarioNoEliminado(usuario);

    securityValidator.validarNoEsUsuarioSystem(usuario.getId(), usuario.getDni());

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

    securityValidator.validarNoEsNombreApellidoReservado(nombre, apellido);

    Usuario usuarioLogueado = securityUtils.usuarioLogueado();

    // Solo pueden editarse a si mismo o a un rol menor
    validarPermisosEdicion(usuario, usuarioLogueado);

    // Armar detalles de auditoria
    Long usuarioAfectadoId = usuario.getId();
    String nombreAnterior = null;
    String nombreNuevo = null;
    String apellidoAnterior = null;
    String apellidoNuevo = null;

    if (!usuario.getNombre().equals(nombre)) {
      nombreAnterior = usuario.getNombre();
      nombreNuevo = nombre;
    }

    if (!usuario.getApellido().equals(apellido)) {
      apellidoAnterior = usuario.getApellido();
      apellidoNuevo = apellido;
    }

    AuditoriaUsuariosDetallesDTO detallesDTO = AuditoriaUsuariosDetallesDTO.builder()
        .usuarioAfectadoId(usuarioAfectadoId)
        .nombreAnterior(nombreAnterior)
        .nombreNuevo(nombreNuevo)
        .apellidoAnterior(apellidoAnterior)
        .apellidoNuevo(apellidoNuevo)
        .build();

    // Si supera los filtros es porque el nombre y apellido son validos
    usuario.setNombre(nombre);
    usuario.setApellido(apellido);

    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    // Verificar si es el mismo usuario
    Usuario usuarioAfectado = usuarioGuardado.getId().equals(usuarioLogueado.getId()) ?
        null : usuarioGuardado;

    auditoriaService.registrarAccion(
        usuarioLogueado,
        usuarioAfectado,
        null,
        TipoAccionAuditoria.NOMBRE_APELLIDO_EDITADO,
        detallesDTO
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

  /// @throws AccionNoPermitidaException 403 Forbidden + errorCode
  /// @throws AccionInvalidaException 403 Forbidden si OWNER/ADMIN intenta modificar a OWNER
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

  /// @throws AccionInvalidaException 403 Forbidden
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

  /// @throws OperacionInvalidaException 400 Bad Request
  private void asegurarUsuarioNoEliminado(Usuario usuario) {
    if (usuario.isEliminado()) {
      throw new OperacionInvalidaException("No puedes modificar un usuario eliminado");
    }
  }

  /// @throws OperacionInvalidaException 400 Bad Request
  private void asegurarUsuarioActivo(Usuario usuario) {
    if (!usuario.isActivo()) {
      throw new OperacionInvalidaException("No puedes realizar la acción en un usuario inactivo");
    }
  }
}
