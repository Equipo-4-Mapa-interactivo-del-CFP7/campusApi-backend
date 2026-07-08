package com.cfp.mapa.controller;

import com.cfp.mapa.dto.jwt.JwtAuthResponseDTO;
import com.cfp.mapa.dto.usuario.UsuarioLoginDTO;
import com.cfp.mapa.security.jwt.JwtProvider;
import com.cfp.mapa.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;

  @PostMapping("/login")
  public ResponseEntity<JwtAuthResponseDTO> authenticateUser(
      @Valid @RequestBody UsuarioLoginDTO loginDTO
  ) {

    String dni = StringUtils.normalizarDni(loginDTO.dni());
    String password = loginDTO.password();

    String dniSinCero = dni.startsWith("0") ? dni.substring(1) : dni;

    if (password.equals("cfp" + dniSinCero) || password.equals("cfp" + dni)) {
      password = "cfp" + dni;
    }

    String dniMarcado = "-" + dni;

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            dniMarcado,
            password
        )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String token = jwtProvider.generarToken(authentication);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(new JwtAuthResponseDTO(token));
  }
}
