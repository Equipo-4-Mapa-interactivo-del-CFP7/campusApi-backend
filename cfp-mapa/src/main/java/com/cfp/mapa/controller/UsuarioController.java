package com.cfp.mapa.controller;

import com.cfp.mapa.dto.usuario.UsuarioAutenticadoDTO;
import com.cfp.mapa.dto.usuario.UsuarioChangePasswordDTO;
import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.service.UsuarioService;
import com.cfp.mapa.validation.ValidDni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  private final UsuarioService usuarioService;

  @PostMapping("/registrar")
  @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
  public ResponseEntity<UsuarioResponseDTO> crearUsuario(
      @Valid @RequestBody UsuarioCreateRequestDTO request
  ) {

    UsuarioResponseDTO response = usuarioService.crearUsuario(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
  public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuariosConFiltro (
      @RequestParam(required = false) String dni,
      @RequestParam(required = false) String nombre,
      @RequestParam(required = false) String apellido,
      @RequestParam(required = false) Boolean activo,
      @PageableDefault(page = 0, size = 10) Pageable pageable
  ) {

    Page<UsuarioResponseDTO> usuarios = usuarioService.listarUsuariosConFiltro(
        dni, nombre, apellido, activo, pageable
    );

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(usuarios);
  }

  @PutMapping("/{id}/restablecer")
  @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
  public ResponseEntity<UsuarioResponseDTO> restablecerPassword(
      @PathVariable Long id) {

    UsuarioResponseDTO response = usuarioService.restablecerPassword(id);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @PutMapping("/{id}/cambiar-activo")
  @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
  public ResponseEntity<UsuarioResponseDTO> cambiarEstadoActivo (
      @PathVariable Long id
  ) {

    UsuarioResponseDTO response = usuarioService.cambiarEstadoActivo(id);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @PutMapping("/me/password")
  public ResponseEntity<Void> cambiarPassword(
      @AuthenticationPrincipal UsuarioAutenticadoDTO usuario,
      @Valid @RequestBody UsuarioChangePasswordDTO request) {

    usuarioService.cambiarPassword(usuario.id(), request.oldPassword(), request.newPassword());

    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }

  @PutMapping("/{dni}/cambiar-rol")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UsuarioResponseDTO> cambiarRolPorAdmin (
      @PathVariable
      @Size(min = 6, max = 15)
      String dni
  ) {

    UsuarioResponseDTO response = usuarioService.cambiarRolPorAdmin(dni);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @GetMapping("/me")
  public ResponseEntity<UsuarioResponseDTO> obtenerMiPerfil (
      @AuthenticationPrincipal UsuarioAutenticadoDTO usuario
  ) {

    UsuarioResponseDTO response = usuarioService.obtenerMiPerfil(usuario.dni());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @GetMapping("/{dni}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UsuarioResponseDTO> obtenerPerfilPorAdmin (
      @PathVariable
      @Size(min = 6, max = 15)
      String dni
  ) {

    UsuarioResponseDTO response = usuarioService.obtenerPerfilPorAdmin(dni);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }
}
