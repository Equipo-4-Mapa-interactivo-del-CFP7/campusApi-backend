package com.cfp.mapa.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.exception.AccionNoPermitidaException;
import com.cfp.mapa.exception.ParametroAccionInvalidoException;
import com.cfp.mapa.security.jwt.JwtProvider;
import com.cfp.mapa.service.AuditoriaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuditoriaController.class)
@EnableMethodSecurity
public class AuditoriaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private JwtProvider jwtProvider;

  @MockitoBean
  private AuthenticationManager authenticationManager;

  @MockitoBean
  private AuditoriaService auditoriaService;

  // =========================================================================
  // ENDPOINT: GET /api/auditorias
  // =========================================================================

  // ÉXITO 200 OK: Un OWNER consulta el historial general de auditorías sin filtrar por usuario
  @Test
  @WithMockUser(roles = "OWNER")
  void listarHistorial_ComoOwnerSinFiltro_DebeDevolver200Ok() throws Exception {
    // GIVEN
    Page<AuditoriaResponseDTO> paginaVacia = new PageImpl<>(List.of());
    // Se agrega null para el parámetro de acción
    when(auditoriaService.listarHistorialPaginado(null, null, 0, 50, "desc")).thenReturn(paginaVacia);

    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .param("page", "0")
            .param("size", "50")
            .param("sort", "desc")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  // ÉXITO 200 OK: Un OWNER consulta el historial filtrando específicamente por el ID de un usuario
  @Test
  @WithMockUser(roles = "OWNER")
  void listarHistorial_ComoOwnerConFiltroUsuario_DebeDevolver200Ok() throws Exception {
    // GIVEN
    Page<AuditoriaResponseDTO> paginaVacia = new PageImpl<>(List.of());
    // Se agrega null para el parámetro de acción
    when(auditoriaService.listarHistorialPaginado(5L, null, 0, 50, "desc")).thenReturn(paginaVacia);

    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .param("usuarioId", "5")
            .param("page", "0")
            .param("size", "50")
            .param("sort", "desc")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  // ÉXITO 200 OK: Un OWNER consulta el historial general ordenado de forma ascendente (sort=asc)
  @Test
  @WithMockUser(roles = "OWNER")
  void listarHistorial_ComoOwnerOrdenAscendente_DebeDevolver200Ok() throws Exception {
    // GIVEN
    Page<AuditoriaResponseDTO> paginaVacia = new PageImpl<>(List.of());
    // Se agrega null para el parámetro de acción
    when(auditoriaService.listarHistorialPaginado(null, null, 0, 50, "asc")).thenReturn(paginaVacia);

    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .param("page", "0")
            .param("size", "50")
            .param("sort", "asc")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "OWNER")
  void listarHistorial_ComoOwnerConAccionValida_DebeDevolver200Ok() throws Exception {
    // GIVEN
    Page<AuditoriaResponseDTO> paginaVacia = new PageImpl<>(List.of());
    when(auditoriaService.listarHistorialPaginado(null, "REPORTE_ATENDIDO", 0, 20, "desc")).thenReturn(paginaVacia);

    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .param("accion", "REPORTE_ATENDIDO")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  // ÉXITO 200 OK: Un OWNER consulta el historial de un usuario específico de forma ascendente (sort=asc)
  @Test
  @WithMockUser(roles = "OWNER")
  void listarHistorial_ComoOwnerConUsuarioYOrdenAscendente_DebeDevolver200Ok() throws Exception {
    // GIVEN
    Page<AuditoriaResponseDTO> paginaVacia = new PageImpl<>(List.of());
    // Se agrega null para el parámetro de acción
    when(auditoriaService.listarHistorialPaginado(5L, null, 0, 50, "asc")).thenReturn(paginaVacia);

    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .param("usuarioId", "5")
            .param("page", "0")
            .param("size", "50")
            .param("sort", "asc")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  // ERROR 400 BAD REQUEST: Se envía un usuarioId alfanumérico inválido en los parámetros de búsqueda
  @Test
  @WithMockUser(roles = "OWNER")
  void listarHistorial_ConUsuarioIdAlfanumericoInvalido_DebeDevolver400BadRequest() throws Exception {
    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .param("usuarioId", "ID_INVALIDO")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 400 BAD REQUEST: Se envía una acción que no existe en el Enum (Service lanza AccionInvalidaException)
  @Test
  @WithMockUser(roles = "OWNER")
  void listarHistorial_ConAccionInvalida_DebeDevolver400BadRequest() throws Exception {
    // GIVEN
    when(auditoriaService.listarHistorialPaginado(null, "ACCION_INVENTADA", 0, 20, "desc"))
        .thenThrow(new ParametroAccionInvalidoException("La acción de auditoría proporcionada no es válida"));

    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .param("accion", "ACCION_INVENTADA")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  // ERROR 401 UNAUTHORIZED: Intento de acceso al historial sin token de autenticación
  @Test
  void listarHistorial_SinAutenticacion_DebeDevolver401Unauthorized() throws Exception {
    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  // ERROR 403 FORBIDDEN: Un ADMIN intenta consultar el historial de auditorías
  @Test
  @WithMockUser(roles = "ADMIN")
  void listarHistorial_ComoAdmin_DebeDevolver403Forbidden() throws Exception {
    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value(nullValue()));
  }

  // ERROR 403 FORBIDDEN: Un usuario con rol PERSONAL intenta consultar el historial de auditorías
  @Test
  @WithMockUser(roles = "PERSONAL")
  void listarHistorial_ComoPersonal_DebeDevolver403Forbidden() throws Exception {
    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value(nullValue()));
  }

  // ERROR 403 FORBIDDEN: La sesión del OWNER fue revocada en base de datos en tiempo real (securityValidator)
  @Test
  @WithMockUser(roles = "OWNER")
  void listarHistorial_ConSesionRevocadaEnBD_DebeDevolver403ForbiddenYSessionInvalidated() throws Exception {
    // GIVEN
    // Se adapta el matcher genérico de 4 argumentos a 5 argumentos usando any()
    doThrow(new AccionNoPermitidaException(
        "Su sesión ya no es válida. Sus permisos han cambiado o su cuenta fue desactivada."
    )).when(auditoriaService).listarHistorialPaginado(any(), any(), anyInt(), anyInt(), anyString());

    // WHEN & THEN
    mockMvc.perform(get("/api/auditorias")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errorCode").value("SESSION_INVALIDATED"));
  }

}
