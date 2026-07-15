package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.dto.auditoria.AuditoriaUsuariosDetallesDTO;
import com.cfp.mapa.dto.metricas.AuditoriaAnaliticaResponseDTO;
import com.cfp.mapa.exception.AuditoriaAnonimizacionException;
import com.cfp.mapa.exception.OperacionInvalidaException;
import com.cfp.mapa.mapper.AuditoriaMapper;
import com.cfp.mapa.model.AuditoriaUsuario;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.model.enums.TipoReporte;
import com.cfp.mapa.repository.AuditoriaRepository;
import com.cfp.mapa.service.AuditoriaService;
import com.cfp.mapa.util.SecurityValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

  private final AuditoriaRepository auditoriaRepository;
  private final AuditoriaMapper auditoriaMapper;
  private final SecurityValidator securityValidator;
  private final ObjectMapper objectMapper;

  @Transactional(readOnly = true)
  @Override
  public Page<AuditoriaResponseDTO> listarHistorialPaginado(
      Long usuarioId,
      List<TipoAccionAuditoria> accion,
      Long reporteId,
      int page,
      int size,
      String order
  ) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER);

    Sort orden = order.equalsIgnoreCase("asc")
        ? Sort.by("fechaAccion").ascending()
        : Sort.by("fechaAccion").descending();

    Pageable pageable = PageRequest.of(page, size, orden);

    Page<AuditoriaUsuario> auditorias = auditoriaRepository.buscarConFiltrosDinamicos(
        usuarioId,
        accion,
        reporteId,
        pageable);

    return auditorias.map(auditoriaMapper::toDTO);
  }

  @Transactional(readOnly = true)
  @Override
  public AuditoriaAnaliticaResponseDTO obtenerAnaliticaEntreFechas(
      LocalDate desde,
      LocalDate hasta,
      Long topEspaciosCriticos
  ) {

    if (desde.isAfter(hasta)) {
      throw new OperacionInvalidaException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }

    // LocalDate -> LocalDateTime
    LocalDateTime desdeDateTime = desde.atStartOfDay();
    LocalDateTime hastaDateTime = hasta.atTime(LocalTime.MAX);

    // Consulta masiva optimizada por el indice idx_auditoria_fecha
    List<AuditoriaUsuario> auditorias = auditoriaRepository.buscarPorRangoFechas(desdeDateTime, hastaDateTime);

    return auditoriaMapper.toAnaliticaResponseDTO(desde, hasta, auditorias, topEspaciosCriticos);

  }

  // ======================================
  // FUNCIONES USADAS POR OTROS SERVICE
  // ======================================

  @Transactional
  @Override
  public void registrarAccion(
      Usuario operador,
      Usuario afectado,
      TipoAccionAuditoria accion
  ) {

    AuditoriaUsuario nuevaAuditoria = new AuditoriaUsuario(
        operador,
        afectado,
        null,
        null,
        null,
        accion,
        null
    );
    auditoriaRepository.save(nuevaAuditoria);
  }

  @Transactional
  @Override
  public void registrarAccion(
      Usuario operador,
      Usuario afectado,
      TipoAccionAuditoria accion,
      Object detalles
  ) {

    String detallesJson = null;

    if (detalles != null) {
      // Convertir DTO en String
      try {
        detallesJson = objectMapper.writeValueAsString(detalles);
      } catch (Exception _) {
      }
    }

    AuditoriaUsuario nuevaAuditoria = new AuditoriaUsuario(
        operador,
        afectado,
        null,
        null,
        null,
        accion,
        detallesJson
    );

    auditoriaRepository.save(nuevaAuditoria);
  }

  @Transactional
  @Override
  public void registrarAccion(
      Usuario operador,
      Usuario afectado,
      Long reporteId,
      Long reporteEspacioId,
      TipoReporte reporteTipo,
      TipoAccionAuditoria accion,
      Object detalles
  ) {

    String detallesJson = null;

    if (detalles != null) {
      // Convertir DTO en String
      try {
        detallesJson = objectMapper.writeValueAsString(detalles);
      } catch (Exception _) {
      }
    }

    AuditoriaUsuario nuevaAuditoria = new AuditoriaUsuario(
        operador,
        afectado,
        reporteId,
        reporteEspacioId,
        reporteTipo,
        accion,
        detallesJson
    );

    auditoriaRepository.save(nuevaAuditoria);
  }

  @Transactional
  @Override
  public void anonimizarUsuario(Long usuarioId, String nombreApellidoAnonimo, String dniAnonimo) {

    // Buscar todas las auditorías donde el usuario haya participado (como operador o afectado)
    List<AuditoriaUsuario> historialAsociado = auditoriaRepository
        .findByOperadorIdOrUsuarioAfectadoId(usuarioId, usuarioId, Pageable.unpaged())
        .getContent();

    if (historialAsociado.isEmpty()) {
      return;
    }

    for (AuditoriaUsuario auditoria : historialAsociado) {

      // Ofuscacion de datos principales
      if (usuarioId.equals(auditoria.getOperadorId())) {
        auditoria.cambiarDatosOperadorAnonimo(nombreApellidoAnonimo, dniAnonimo);
      }

      if (usuarioId.equals(auditoria.getUsuarioAfectadoId())) {
        auditoria.cambiarDatosAfectadoAnonimo(nombreApellidoAnonimo, dniAnonimo);
      }

      // Ofuscacion del JSON de detalles internos (si existen)
      if (auditoria.getDetalles() != null && !auditoria.getDetalles().isBlank()) {
        try {
          // Deserializar el JSON string al DTO estructurado
          AuditoriaUsuariosDetallesDTO detalles = objectMapper.readValue(
              auditoria.getDetalles(),
              AuditoriaUsuariosDetallesDTO.class
          );

          // Comprobar si los detalles pertenecen al usuario afectado que se esta borrando
          if (usuarioId.equals(detalles.usuarioAfectadoId())) {

            // Reconstruir el DTO pisando únicamente los campos de texto PII
            AuditoriaUsuariosDetallesDTO detallesAnonimos = AuditoriaUsuariosDetallesDTO.builder()
                .usuarioAfectadoId(detalles.usuarioAfectadoId())
                .rolAnterior(detalles.rolAnterior())
                .rolNuevo(detalles.rolNuevo())
                .activoAnterior(detalles.activoAnterior())
                .activoNuevo(detalles.activoNuevo())
                // Ofuscar las cadenas de texto personales si estaban presentes
                .dniAnterior(detalles.dniAnterior() != null ? dniAnonimo : null)
                .dniNuevo(detalles.dniNuevo() != null ? dniAnonimo : null)
                .nombreAnterior(detalles.nombreAnterior() != null ? nombreApellidoAnonimo : null)
                .nombreNuevo(detalles.nombreNuevo() != null ? nombreApellidoAnonimo : null)
                .apellidoAnterior(detalles.apellidoAnterior() != null ? nombreApellidoAnonimo : null)
                .apellidoNuevo(detalles.apellidoNuevo() != null ? nombreApellidoAnonimo : null)
                .build();

            // Convertir el JSON en String y reemplazar
            String jsonAnonimo = objectMapper.writeValueAsString(detallesAnonimos);
            auditoria.anonimizarDetalles(jsonAnonimo);
          }
        } catch (JacksonException ex) {
          throw new AuditoriaAnonimizacionException(
              "No se pudo procesar el JSON de detalles en la anonimización para el usuario ID: " + usuarioId,
              ex
          );
        }
      }
    }

    auditoriaRepository.saveAll(historialAsociado);
  }
}