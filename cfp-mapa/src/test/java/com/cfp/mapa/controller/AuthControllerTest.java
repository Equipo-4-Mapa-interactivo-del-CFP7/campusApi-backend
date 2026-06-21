package com.cfp.mapa.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cfp.mapa.dto.usuario.UsuarioLoginDTO;
import com.cfp.mapa.security.jwt.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private AuthenticationManager authenticationManager;

  @MockitoBean
  private JwtProvider jwtProvider;

  // =========================================================================
  // VALIDACIONES DE ESTRUCTURA (DTO / @Valid)
  // =========================================================================

  // ERROR 400 BAD REQUEST: Intento de login con DNI vacío
  @Test
  void authenticateUser_ConDniVacio_DebeDevolver400BadRequest() throws Exception {
    //GIVEN
    UsuarioLoginDTO loginDTO = new UsuarioLoginDTO("", "password123");

    // WHEN & THEN
    mockMvc.perform(post("/api/auth/login")
           .contentType(MediaType.APPLICATION_JSON)
           .content(objectMapper.writeValueAsString(loginDTO)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 400 BAD REQUEST: Intento de login con contraseña vacía
  @Test
  void authenticateUser_ConPasswordVacio_DebeDevolver400BadRequest() throws Exception {
    // GIVEN
    UsuarioLoginDTO loginDTO = new UsuarioLoginDTO("12345678", "");

    // WHEN & THEN
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTO)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 400 BAD REQUEST: Intento de login con DNI y contraseña vacíos
  @Test
  void authenticateUser_ConDniYPasswordVacios_DebeDevolver400BadRequest() throws Exception {
    // GIVEN
    UsuarioLoginDTO loginDTO = new UsuarioLoginDTO("", "");

    // WHEN & THEN
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTO)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 400 BAD REQUEST: Intento de login con un formato de DNI inválido
  @Test
  void authenticateUser_ConDniInvalido_DebeDevolver400BadRequest() throws Exception {
    // GIVEN
    UsuarioLoginDTO loginDTO = new UsuarioLoginDTO("123", "password123");

    // WHEN & THEN
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTO)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 400 BAD REQUEST: Intento de login con una contraseña que no cumple los mínimos requeridos
  @Test
  void authenticateUser_ConPasswordInvalida_DebeDevolver400BadRequest() throws Exception {
    // GIVEN
    UsuarioLoginDTO loginDTO = new UsuarioLoginDTO("12345678", "123");

    // WHEN & THEN
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTO)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // =========================================================================
  // LÓGICA DE NEGOCIO (Autenticación / Credenciales)
  // =========================================================================

  // ÉXITO 200 OK: Credenciales correctas, retorna el token de acceso
  @Test
  void authenticateUser_ConCredencialesCorrectas_DebeDevolver200YToken() throws Exception {
    // GIVEN
    UsuarioLoginDTO loginDTO = new UsuarioLoginDTO("12345678", "administrador");
    String tokenSimulado = "jwt-falso-de-prueba-12345";
    Authentication authFalsa = mock(Authentication.class);

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authFalsa);
    when(jwtProvider.generarToken(authFalsa))
        .thenReturn(tokenSimulado);

    // WHEN & THEN
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTO)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(tokenSimulado));
  }

  // ERROR 401 UNAUTHORIZED: Intento de login con contraseña incorrecta
  @Test
  void authenticateUser_ConPasswordIncorrecto_DebeLanzarBadCredentialsException() throws Exception {
    // GIVEN
    UsuarioLoginDTO loginDTO = new UsuarioLoginDTO("12345678", "clave_incorrecta");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Credenciales inválidas"));

    // WHEN & THEN
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTO)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message")
            .value("Credenciales incorrectas (DNI o contraseña inválidos)"));
  }

  // ERROR 401 UNAUTHORIZED: Intento de login con un usuario desactivado en el sistema
  @Test
  void authenticateUser_ConCuentaDesactivada_DebeDevolver401ConMensajeDeBloqueo() throws Exception {
    // GIVEN
    UsuarioLoginDTO loginDTO = new UsuarioLoginDTO("12345678", "administrador");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new org.springframework.security.authentication.DisabledException("Cuenta inactivada"));

    // WHEN & THEN
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTO)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message")
            .value("Tu cuenta se encuentra temporalmente desactivada"));
  }
}

