package com.cfp.mapa.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.exception.AccionInvalidaException;
import com.cfp.mapa.exception.AccionNoPermitidaException;
import com.cfp.mapa.exception.DniDuplicadoException;
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

  // --- ESCENARIO 1: EXITO 201 CREATED [OWNER CREA UN ADMIN] ---
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

  // --- ESCENARIO 2: ERROR 403 FORBIDDEN [PERSONAL ACCEDE AL ENDPOINT] ---
  @Test
  @WithMockUser(roles = "PERSONAL")
  void crearUsuario_ComoPersonal_DebeDevolver403Forbidden() throws Exception {
    // WHEN & THEN
    mockMvc.perform(post("/api/usuarios/registrar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDtoPersonalValido)))
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  // --- ESCENARIO 3: ERROR 403 FORBIDDEN [ADMIN INTENTA CREAR UN ADMIN] ---
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
        .andExpect(status().isForbidden());
  }

  // --- ESCENARIO 4: ERROR 400 BAD REQUEST [SE INGRESA UN DTO INVALIDO] ---
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

  // --- ESCENARIO 5: ERROR 401 UNAUTHORIZED [SE INTENTA ACCEDER AL ENDPOINT SIN ESTAR LOGUEADO] ---
  @Test
  void crearUsuario_SinTokenOAnonimo_DebeDevolver401Unauthorized() throws Exception {
    // WHEN & THEN
    mockMvc.perform(post("/api/usuarios/registrar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDtoAdminValido)))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // --- ESCENARIO 6: ERROR 409 CONFLICT [EL DNI DEL NUEVO USUARIO YA ESTA REGISTRADO] ---
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

  // --- ESCENARIO 7: ERROR 403 FORBIDDEN [TOKEN MODIFICADO / PERMISOS REVOCADOS EN BD] ---
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

  // --- ESCENARIO 1: EXITO 200 OK [OWNER OBTIENE LISTA COMPLETA] --
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

  // --- ESCENARIO 2: EXITO 200 OK [ADMIN OBTIENE LISTA COMPLETA] ---
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

  // --- ESCENARIO 3: EXITO 200 OK [ADMIN OBTIENE LISTA FILTRADA POR nombre] ---
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

  // --- ESCENARIO 4: ERROR 403 FORBIDDEN [PERSONAL INTENTA OBTENER LISTA COMPLETA] ---
  @Test
  @WithMockUser(roles = "PERSONAL")
  void listarUsuarios_ComoPersonal_DebeDevolver403Forbidden() throws Exception {
    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios"))
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  // --- ESCENARIO 5: ERROR 400 BAD REQUEST [FILTRADA POR BOOLEAN ACTIVO CON STRING INVALIDO] ---
  @Test
  @WithMockUser(roles = "ADMIN")
  void listarUsuarios_ConActivoInvalido_DebeDevolver400BadRequest() throws Exception {
    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios")
            .param("activo", "hola"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // --- ESCENARIO 6: ERROR 401 UNAUTHORIZED [SE INTENTA USAR EL ENDPOINT SIN ESTAR LOGUEADO] ---
  @Test
  void listarUsuarios_SinTokenOAnonimo_DebeDevolver401Unauthorized() throws Exception {
    // WHEN & THEN
    mockMvc.perform(get("/api/usuarios"))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // --- ESCENARIO 7: ERROR 403 FORBIDDEN [TOKEN MODIFICADO / PERMISOS REVOCADOS EN BD] ---
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

  // --- ESCENARIO 8: ERROR 400 BAD REQUEST [SE FILTRA POR UN ROL INEXISTENTE] ---
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

  // --- ESCENARIO 9: ERROR 401 UNAUTHORIZED [EL TOKEN ES VALIDO PERO EL USUARIO ESTA DESACTIVADO EN EL FILTRO] ---
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

  // =========================================================================
  // ENDPOINT: PUT /api/usuarios/{id}/cambiar-activo
  // =========================================================================

  // =========================================================================
  // ENDPOINT: PUT /api/usuarios/me/password
  // =========================================================================

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
