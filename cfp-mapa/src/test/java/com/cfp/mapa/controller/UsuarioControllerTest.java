package com.cfp.mapa.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cfp.mapa.dto.usuario.UsuarioChangePasswordDTO;
import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.exception.AccionInvalidaException;
import com.cfp.mapa.exception.AccionNoPermitidaException;
import com.cfp.mapa.exception.DniDuplicadoException;
import com.cfp.mapa.exception.PasswordIncorrectaException;
import com.cfp.mapa.exception.UsuarioNotFoundException;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.security.jwt.JwtProvider;
import com.cfp.mapa.service.UsuarioService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UsuarioController.class)
@EnableMethodSecurity
public class UsuarioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UsuarioService usuarioService;

  @MockitoBean
  private JwtProvider jwtProvider;

  @MockitoBean
  private AuthenticationManager authenticationManager;

  private UsuarioResponseDTO usuarioOwnerResponse;
  private UsuarioResponseDTO usuarioAdminReponse;
  private UsuarioResponseDTO usuarioPersonalReponse;
  private UsuarioResponseDTO usuarioChangePasswordResponse;

  private UsuarioCreateRequestDTO createDtoAdminValido;
  private UsuarioCreateRequestDTO createDtoPersonalValido;

  @BeforeEach
  void setUp() {
    usuarioOwnerResponse = new UsuarioResponseDTO(
        1L, "11111111", Rol.OWNER, "Perfil Ow", "Owner", true
    );

    usuarioAdminReponse = new UsuarioResponseDTO(
        2L, "22222222", Rol.ADMIN, "Perfil Ad", "Admin", true
    );

    usuarioPersonalReponse = new UsuarioResponseDTO(
        3L, "33333333", Rol.PERSONAL, "Perfil Pe", "Personal", true
    );

    usuarioChangePasswordResponse = new UsuarioResponseDTO(
        4L, "44444444", Rol.CHANGE_PASSWORD, "Perfil Ch", "Change Password", true
    );

    createDtoAdminValido = new UsuarioCreateRequestDTO(
        "22222222", "Perfil Ad", "Admin", "ADMIN"
    );

    createDtoPersonalValido = new UsuarioCreateRequestDTO(
        "33333333", "Perfil Pe", "Personal", "PERSONAL"
    );
  }

  // =========================================================================
  // ENDPOINT: POST /api/usuarios/registrar
  // =========================================================================

  // EXITO 201 CREATED: OWNER crea un ADMIN
  @Test
  @WithMockUser(roles = "OWNER")
  void crearUsuario_ComoOwner_DebeDevolver201Created() throws Exception {
    // GIVEN
    when(usuarioService.crearUsuario(any(UsuarioCreateRequestDTO.class)))
        .thenReturn(usuarioChangePasswordResponse);

    // WHEN & THEN
    mockMvc.perform(post("/api/usuarios/registrar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDtoAdminValido)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rol").value("CHANGE_PASSWORD"));
  }

  // ERROR 403 FORBIDDEN: PERSONAL accede al endpoint
  @Test
  @WithMockUser(roles = "PERSONAL")
  void crearUsuario_ComoPersonal_DebeDevolver403Forbidden() throws Exception {
    // WHEN & THEN
    mockMvc.perform(post("/api/usuarios/registrar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDtoPersonalValido)))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value(nullValue()));
  }

  // ERROR 403 FORBIDDEN: ADMIN intenta crear un ADMIN
  @Test
  @WithMockUser(roles = "ADMIN")
  void crearUsuario_ComoAdminRegistrandoAdmin_DebeDevolver403Forbidden() throws Exception {
    // GIVEN
    when(usuarioService.crearUsuario(any(UsuarioCreateRequestDTO.class)))
        .thenThrow(new AccionInvalidaException("Un ADMIN solo puede crear PERSONAL"));

    // WHEN & THEN
    mockMvc.perform(post("/api/usuarios/registrar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDtoAdminValido)))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value(nullValue()));
  }

  // ERROR 400 BAD REQUEST: Se ingresa un dto invalido
  @Test
  @WithMockUser(roles = "OWNER")
  void crearUsuario_ConDatosInvalidos_DebeDevolver400BadRequest() throws Exception {
    // GIVEN: Forzamos un DTO inválido rompiendo el DNI (vacío o nulo)
    UsuarioCreateRequestDTO dtoInvalido = new UsuarioCreateRequestDTO("", "Perfil", "Admin", "ADMIN");

    // WHEN & THEN (El @Valid del controlador hace saltar MethodArgumentNotValidException)
    mockMvc.perform(post("/api/usuarios/registrar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dtoInvalido)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 401 UNAUTHORIZED: Se intenta acceder al endpoint sin estar logueado
  @Test
  void crearUsuario_SinTokenOAnonimo_DebeDevolver401Unauthorized() throws Exception {
    // WHEN & THEN
    mockMvc.perform(post("/api/usuarios/registrar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDtoAdminValido)))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // ERROR 409 CONFLICT: El dni del nuevo usuario ya esta registrado
  @Test
  @WithMockUser(roles = "OWNER")
  void crearUsuario_ConDniDuplicado_DebeDevolver409Conflict() throws Exception {
    // GIVEN: Obligamos al servicio a lanzar tu excepción personalizada
    when(usuarioService.crearUsuario(any(UsuarioCreateRequestDTO.class)))
        .thenThrow(new DniDuplicadoException("55555555"));

    // WHEN & THEN (Tu GlobalExceptionHandler la traduce a 409 CONFLICT)
    mockMvc.perform(post("/api/usuarios/registrar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDtoAdminValido)))
        .andDo(print())
        .andExpect(status().isConflict());
  }

  // ERROR 403 FORBIDDEN: Token modificado / permisos revocados en BD
  @Test
  @WithMockUser(roles = "OWNER")
  void crearUsuario_ConTokenModificadoOPermisosRevocados_DebeDevolver403ForbiddenYSessionInvalidated() throws Exception {
    // GIVEN: El usuario simula venir con rol OWNER, pero el servicio detecta en BD que sus permisos cambiaron o se desactivó
    when(usuarioService.crearUsuario(any(UsuarioCreateRequestDTO.class)))
        .thenThrow(new AccionNoPermitidaException(
            "Su sesión ya no es válida. Sus permisos han cambiado o su cuenta fue desactivada."
        ));

    // WHEN & THEN
    mockMvc.perform(post("/api/usuarios/registrar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDtoAdminValido)))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("SESSION_INVALIDATED"));
  }

  // =========================================================================
  // ENDPOINT: GET /api/usuarios
  // =========================================================================

  // EXITO 200 OK: OWNER obtiene lista completa
  @Test
  @WithMockUser(roles = "OWNER")
  void listarUsuarios_ComoOwner_DebeDevolver200OKConPagina() throws Exception {
    // GIVEN
    Page<UsuarioResponseDTO> paginaSimulada = new PageImpl<>(
        List.of(usuarioOwnerResponse, usuarioAdminReponse));
    when(usuarioService.listarUsuariosConFiltro(any(), any(), any(), any(), any(), any(Pageable.class)))
        .thenReturn(paginaSimulada);

    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios")
            .param("page", "0")
            .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].dni").value("11111111"));
  }

  // EXITO 200 OK: ADMIN obtiene lista completa
  @Test
  @WithMockUser(roles = "ADMIN")
  void listarUsuarios_ComoAdmin_DebeDevolver200OKConPagina() throws Exception {
    // GIVEN
    Page<UsuarioResponseDTO> paginaSimulada = new PageImpl<>(List.of(usuarioAdminReponse, usuarioPersonalReponse));
    when(usuarioService.listarUsuariosConFiltro(any(), any(), any(), any(), any(), any(Pageable.class)))
        .thenReturn(paginaSimulada);

    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].dni").value("22222222"));
  }

  // EXITO 200 OK: ADMIN obtiene lista filtrada por nombre
  @Test
  @WithMockUser(roles = "ADMIN")
  void listarUsuarios_ConFiltroNombre_DebeDevolver200OKConPaginaFiltrada() throws Exception {
    // GIVEN
    Page<UsuarioResponseDTO> paginaFiltrada = new PageImpl<>(List.of(usuarioAdminReponse));
    when(usuarioService.listarUsuariosConFiltro(eq(null), eq("Perfil Ad"), eq(null), eq(null), eq(null), any(Pageable.class)))
        .thenReturn(paginaFiltrada);

    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios")
            .param("nombre", "Perfil Ad"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].nombre").value("Perfil Ad"));
  }

  // ERROR 403 FORBIDDEN: PERSONAL intenta obtener lista completa
  @Test
  @WithMockUser(roles = "PERSONAL")
  void listarUsuarios_ComoPersonal_DebeDevolver403Forbidden() throws Exception {
    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value(nullValue()));
  }

  // ERROR 400 BAD REQUEST: Filtrado por boolean activo con string invalido
  @Test
  @WithMockUser(roles = "ADMIN")
  void listarUsuarios_ConActivoInvalido_DebeDevolver400BadRequest() throws Exception {
    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios")
            .param("activo", "hola"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 401 UNAUTHORIZED: Se intenta usar el endpoint sin estar logueado
  @Test
  void listarUsuarios_SinTokenOAnonimo_DebeDevolver401Unauthorized() throws Exception {
    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios"))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // ERROR 403 FORBIDDEN: Token modificado / permisos revocados en BD
  @Test
  @WithMockUser(roles = "OWNER")
  void listarUsuarios_ConTokenModificadoOPermisosRevocados_DebeDevolver403ForbiddenYSessionInvalidated() throws Exception {
    // GIVEN
    when(usuarioService.listarUsuariosConFiltro(any(), any(), any(), any(), any(), any(Pageable.class)))
        .thenThrow(new AccionNoPermitidaException(
            "Su sesión ya no es válida. Sus permisos han cambiado o su cuenta fue desactivada."
        ));

    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("SESSION_INVALIDATED"));
  }

  // ERROR 400 BAD REQUEST: Se filtra por un rol inexistente
  @Test
  @WithMockUser(roles = "ADMIN")
  void listarUsuarios_ConRolInexistente_DebeDevolver500InternalServerError() throws Exception {
    // GIVEN: Forzamos el comportamiento que genera Rol.valueOf("INVALIDO") de forma nativa
    when(usuarioService.listarUsuariosConFiltro(any(), any(), any(), any(), eq("INVALIDO"), any(Pageable.class)))
        .thenThrow(new IllegalArgumentException("No enum constant com.cfp.mapa.model.Rol.INVALIDO"));

    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios")
            .param("rol", "INVALIDO"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 401 UNAUTHORIZED: El token es valido pero el usuario esta desactivado en el filtro
  @Test
  void listarUsuarios_ConTokenValidoPeroUsuarioDesactivado_DebeDevolver401Unauthorized() throws Exception {
    // GIVEN: El token es válido estructuralmente, pero al procesarlo, Spring Security
    // detecta que la cuenta está desactivada (DisabledException) antes de llegar al controlador.

    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios")
            .header("Authorization", "Bearer token_con_usuario_desactivado"))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // =========================================================================
  // ENDPOINT: PUT /api/usuarios/{id}/restablecer
  // =========================================================================

  // ÉXITO 200 OK: OWNER restablece la contraseña de un ADMIN
  @Test
  @WithMockUser(roles = "OWNER")
  void restablecerPassword_ComoOwnerAAdmin_DebeDevolver200OK() throws Exception {
    // GIVEN: Usamos el DTO del ADMIN configurado en el setUp pero simulando el cambio de rol
    when(usuarioService.restablecerPassword(2L)).thenReturn(usuarioChangePasswordResponse);

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/restablecer", 2L))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rol").value("CHANGE_PASSWORD"));
  }

  // ÉXITO 200 OK: ADMIN restablece la contraseña de un PERSONAL
  @Test
  @WithMockUser(roles = "ADMIN")
  void restablecerPassword_ComoAdminAPersonal_DebeDevolver200OK() throws Exception {
    // GIVEN: El servicio procesa al PERSONAL y retorna la respuesta correspondiente
    when(usuarioService.restablecerPassword(3L)).thenReturn(usuarioChangePasswordResponse);

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/restablecer", 3L))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rol").value("CHANGE_PASSWORD"));
  }

  // ERROR 400 BAD REQUEST: Se envía un identificador en formato alfanumérico inválido para el tipo Long
  @Test
  @WithMockUser(roles = "ADMIN")
  void restablecerPassword_ConIdAlfanumericoInvalido_DebeDevolver400BadRequest() throws Exception {
    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/restablecer", "ABC1234567"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 403 FORBIDDEN: OWNER intenta restablecer la contraseña de otro OWNER (Restricción de Jerarquía)
  @Test
  @WithMockUser(roles = "OWNER")
  void restablecerPassword_AUnOwner_DebeDevolver400BadRequest() throws Exception {
    // GIVEN
    when(usuarioService.restablecerPassword(1L))
        .thenThrow(new AccionInvalidaException("No se puede restablecer la contraseña del dueño del sistema."));

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/restablecer", 1L))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("No se puede restablecer la contraseña del dueño del sistema."))
        .andExpect(jsonPath("$.errorCode").value(nullValue()));
  }

  // ERROR 403 FORBIDDEN: Se intenta restablecer la contraseña a un usuario que ya tiene un cambio pendiente
  @Test
  @WithMockUser(roles = "ADMIN")
  void restablecerPassword_ConRestablecimientoPendiente_DebeDevolver400BadRequest() throws Exception {
    // GIVEN
    when(usuarioService.restablecerPassword(4L))
        .thenThrow(new AccionInvalidaException("El usuario ya tiene un restablecimiento de contraseña pendiente."));

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/restablecer", 4L))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("El usuario ya tiene un restablecimiento de contraseña pendiente."))
        .andExpect(jsonPath("$.errorCode").value(nullValue()));
  }

  // ERROR 401 UNAUTHORIZED: Se intenta acceder al endpoint sin estar logueado
  @Test
  void restablecerPassword_SinAutenticacion_DebeDevolver401Unauthorized() throws Exception {
    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/2/restablecer"))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // ERROR 401 UNAUTHORIZED: El token de acceso es válido pero el usuario fue desactivado en los filtros de entrada
  @Test
  void restablecerPassword_ConUsuarioDesactivadoEnFiltro_DebeDevolver401Unauthorized() throws Exception {
    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/restablecer", 2L)
            .header("Authorization", "Bearer token_desactivado_ejemplo"))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // ERROR 403 FORBIDDEN: Un usuario con rol PERSONAL intenta acceder al endpoint
  @Test
  @WithMockUser(roles = "PERSONAL")
  void restablecerPassword_ComoPersonal_DebeDevolver403Forbidden() throws Exception {
    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/2/restablecer"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value(nullValue()));
  }

  // ERROR 403 FORBIDDEN: La sesión del ADMIN fue revocada en la base de datos en tiempo real
  @Test
  @WithMockUser(roles = "ADMIN")
  void restablecerPassword_ConSesionRevocadaEnBD_DebeDevolver403ForbiddenYSessionInvalidated() throws Exception {
    // GIVEN
    when(usuarioService.restablecerPassword(2L))
        .thenThrow(new AccionNoPermitidaException(
            "Su sesión ya no es válida. Sus permisos han cambiado o su cuenta fue desactivada."
        ));

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/restablecer", 2L))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("SESSION_INVALIDATED"));
  }

  // ERROR 404 NOT FOUND: Se intenta restablecer la contraseña de un identificador de usuario que no existe
  @Test
  @WithMockUser(roles = "ADMIN")
  void restablecerPassword_ConUsuarioInexistente_DebeDevolver404NotFound() throws Exception {
    // GIVEN
    when(usuarioService.restablecerPassword(99L))
        .thenThrow(new UsuarioNotFoundException(99L));

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/restablecer", 99L))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  // =========================================================================
  // ENDPOINT: PUT /api/usuarios/{id}/cambiar-activo
  // =========================================================================

  // ÉXITO 200 OK: OWNER cambia el estado activo de un ADMIN
  @Test
  @WithMockUser(roles = "OWNER")
  void cambiarEstadoActivo_ComoOwnerAAdmin_DebeDevolver200OK() throws Exception {
    // GIVEN: El servicio realiza el toggle del estado activo
    when(usuarioService.cambiarEstadoActivo(2L)).thenReturn(usuarioAdminReponse);

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/cambiar-activo", 2L))
        .andDo(print())
        .andExpect(status().isOk());
  }

  // ÉXITO 200 OK: ADMIN cambia el estado activo de un PERSONAL
  @Test
  @WithMockUser(roles = "ADMIN")
  void cambiarEstadoActivo_ComoAdminAPersonal_DebeDevolver200OK() throws Exception {
    // GIVEN
    when(usuarioService.cambiarEstadoActivo(3L)).thenReturn(usuarioPersonalReponse);

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/cambiar-activo", 3L))
        .andDo(print())
        .andExpect(status().isOk());
  }

  // ÉXITO 200 OK: ADMIN cambia el estado activo de un usuario con cambio de clave pendiente
  @Test
  @WithMockUser(roles = "ADMIN")
  void cambiarEstadoActivo_ComoAdminAUsuarioConClavePendiente_DebeDevolver200OK() throws Exception {
    // GIVEN: Valida que el estado CHANGE_PASSWORD no bloquee el flujo
    when(usuarioService.cambiarEstadoActivo(4L)).thenReturn(usuarioChangePasswordResponse);

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/cambiar-activo", 4L))
        .andDo(print())
        .andExpect(status().isOk());
  }

  // ERROR 400 BAD REQUEST: Se envía un identificador en formato alfanumérico inválido para el tipo Long
  @Test
  @WithMockUser(roles = "ADMIN")
  void cambiarEstadoActivo_ConIdAlfanumericoInvalido_DebeDevolver400BadRequest() throws Exception {
    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/cambiar-activo", "ABC12345"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 401 UNAUTHORIZED: Se intenta acceder al endpoint sin estar logueado
  @Test
  void cambiarEstadoActivo_SinAutenticacion_DebeDevolver401Unauthorized() throws Exception {
    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/2/cambiar-activo"))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // ERROR 401 UNAUTHORIZED: El token de acceso es válido pero el usuario fue desactivado en los filtros de entrada
  @Test
  void cambiarEstadoActivo_ConUsuarioDesactivadoEnFiltro_DebeDevolver401Unauthorized() throws Exception {
    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/cambiar-activo", 2L)
            .header("Authorization", "Bearer token_desactivado_ejemplo"))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // ERROR 403 FORBIDDEN: Un usuario con rol PERSONAL intenta acceder al endpoint
  @Test
  @WithMockUser(roles = "PERSONAL")
  void cambiarEstadoActivo_ComoPersonal_DebeDevolver403Forbidden() throws Exception {
    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/2/cambiar-activo"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value(nullValue()));
  }

  // ERROR 403 FORBIDDEN: OWNER intenta modificar el estado de otro OWNER (Restricción de Jerarquía)
  @Test
  @WithMockUser(roles = "OWNER")
  void cambiarEstadoActivo_AUnOwner_DebeDevolver403Forbidden() throws Exception {
    // GIVEN
    when(usuarioService.cambiarEstadoActivo(1L))
        .thenThrow(new AccionInvalidaException("No se puede cambiar el estado del dueño del sistema"));

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/cambiar-activo", 1L))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("No se puede cambiar el estado del dueño del sistema"))
        .andExpect(jsonPath("$.errorCode").value(nullValue()));
  }

  // ERROR 403 FORBIDDEN: La sesión del ADMIN fue revocada en la base de datos en tiempo real
  @Test
  @WithMockUser(roles = "ADMIN")
  void cambiarEstadoActivo_ConSesionRevocadaEnBD_DebeDevolver403ForbiddenYSessionInvalidated() throws Exception {
    // GIVEN
    when(usuarioService.cambiarEstadoActivo(2L))
        .thenThrow(new AccionNoPermitidaException(
            "Su sesión ya no es válida. Sus permisos han cambiado o su cuenta fue desactivada."
        ));

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/cambiar-activo", 2L))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("SESSION_INVALIDATED"));
  }

  // ERROR 404 NOT FOUND: Se intenta modificar el estado de un identificador de usuario que no existe
  @Test
  @WithMockUser(roles = "ADMIN")
  void cambiarEstadoActivo_ConUsuarioInexistente_DebeDevolver404NotFound() throws Exception {
    // GIVEN
    when(usuarioService.cambiarEstadoActivo(99L))
        .thenThrow(new UsuarioNotFoundException(99L));

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/{id}/cambiar-activo", 99L))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  // =========================================================================
  // ENDPOINT: PUT /api/usuarios/me/password
  // =========================================================================

  // ÉXITO 200 OK: El usuario cambia su contraseña correctamente
  @Test
  @WithMockUser(roles = "PERSONAL")
  void cambiarPassword_ConDatosValidos_DebeDevolver200Ok() throws Exception {
    // GIVEN
    UsuarioChangePasswordDTO request = new UsuarioChangePasswordDTO("Password123", "NuevaPassword123");

    // Como el controlador extrae el principal del contexto, no requerimos un 'when' del service que devuelva datos ya que es void
    doNothing().when(usuarioService).cambiarPassword(anyLong(), anyString(), anyString());

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  // ÉXITO 200 OK: Un usuario con rol CHANGE_PASSWORD cambia su contraseña correctamente
  @Test
  @WithMockUser(roles = "CHANGE_PASSWORD")
  void cambiarPassword_ComoUsuarioConCambioPendiente_DebeDevolver200Ok() throws Exception {
    // GIVEN
    UsuarioChangePasswordDTO request = new UsuarioChangePasswordDTO("cfp12345678", "MiNuevaClaveSegura123");
    doNothing().when(usuarioService).cambiarPassword(anyLong(), anyString(), anyString());

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  // ERROR 400 BAD REQUEST: Intento de cambio con contraseñas vacías o que rompen el @Valid (Validación DTO)
  @Test
  @WithMockUser(roles = "PERSONAL")
  void cambiarPassword_ConContrasenasVacias_DebeDevolver400BadRequest() throws Exception {
    // GIVEN: Contraseñas que rompen el regex o restricciones NotBlank
    UsuarioChangePasswordDTO request = new UsuarioChangePasswordDTO("", "   ");

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 400 BAD REQUEST: La nueva contraseña es idéntica al DNI o por defecto de seguridad (Regla de Negocio)
  @Test
  @WithMockUser(roles = "PERSONAL")
  void cambiarPassword_ConNuevaPasswordIgualADni_DebeDevolver400BadRequest() throws Exception {
    // GIVEN
    UsuarioChangePasswordDTO request = new UsuarioChangePasswordDTO("ClaveActual123", "cfp12345678");

    // Usamos any() o especificamos adecuadamente para que intercepte la firma sin importar los nulos del principal
    doThrow(new PasswordIncorrectaException("No puedes usar esta contraseña"))
        .when(usuarioService).cambiarPassword(any(), any(), any());

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("No puedes usar esta contraseña"));
  }

  // ERROR 400 BAD REQUEST: La contraseña actual ('oldPassword') ingresada es incorrecta
  @Test
  @WithMockUser(roles = "PERSONAL")
  void cambiarPassword_ConPasswordActualIncorrecta_DebeDevolver400BadRequest() throws Exception {
    // GIVEN
    UsuarioChangePasswordDTO request = new UsuarioChangePasswordDTO("ClaveErronea", "NuevaClave123");

    // 💡 Usamos any() genérico para asegurar que Mockito intercepte la firma del void
    doThrow(new PasswordIncorrectaException())
        .when(usuarioService).cambiarPassword(any(), any(), any());

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("La contraseña ingresada es incorrecta"));
  }

  // ERROR 401 UNAUTHORIZED: Se intenta acceder al endpoint sin estar logueado
  @Test
  void cambiarPassword_SinAutenticacion_DebeDevolver401Unauthorized() throws Exception {
    // GIVEN
    UsuarioChangePasswordDTO request = new UsuarioChangePasswordDTO("ClaveActual123", "NuevaClave123");

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // ERROR 401 UNAUTHORIZED: El token es estructuralmente válido pero el usuario fue desactivado en los filtros
  @Test
  void cambiarPassword_ConUsuarioDesactivadoEnFiltro_DebeDevolver401Unauthorized() throws Exception {
    // GIVEN
    UsuarioChangePasswordDTO request = new UsuarioChangePasswordDTO("ClaveActual123", "NuevaClave123");

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/me/password")
            .header("Authorization", "Bearer token_desactivado_ejemplo")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // ERROR 403 FORBIDDEN: La sesión del usuario fue revocada en la base de datos en tiempo real
  @Test
  @WithMockUser(roles = "PERSONAL")
  void cambiarPassword_ConSesionRevocadaEnBD_DebeDevolver403ForbiddenYSessionInvalidated() throws Exception {
    // GIVEN
    UsuarioChangePasswordDTO request = new UsuarioChangePasswordDTO("ClaveActual123", "NuevaClave123");

    doThrow(new AccionNoPermitidaException(
        "Su sesión ya no es válida. Sus permisos han cambiado o su cuenta fue desactivada."
    )).when(usuarioService).cambiarPassword(any(), any(), any());

    // WHEN & THEN
    mockMvc.perform(put("/api/usuarios/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("SESSION_INVALIDATED"));
  }

  // =========================================================================
  // ENDPOINT: PUT /api/usuarios/{id}/cambiar-rol
  // =========================================================================

  // =========================================================================
  // ENDPOINT: GET /api/usuarios/me
  // =========================================================================

  // =========================================================================
  // ENDPOINT: GET /api/usuarios/{id}
  // =========================================================================

  // =========================================================================
  // ENDPOINT: POST /api/usuarios/{id}/eliminar
  // =========================================================================

  // =========================================================================
  // ENDPOINT: POST /api/usuarios/recuperar-owner
  // =========================================================================

  // =========================================================================
  // ENDPOINT: POST /api/usuarios/{id}/transferir-owner
  // =========================================================================
}
