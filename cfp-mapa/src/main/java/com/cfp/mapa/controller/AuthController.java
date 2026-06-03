package com.cfp.mapa.controller;

import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.service.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UsuarioServiceImpl usuarioService;

  @PostMapping("/registrar")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UsuarioResponseDTO> crearUsuarioPorAdmin(
      @Valid @RequestBody UsuarioCreateRequestDTO request
  ) {

    UsuarioResponseDTO response = usuarioService.crearUsuarioPorAdmin(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }
}
