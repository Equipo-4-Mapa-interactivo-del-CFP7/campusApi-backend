package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.exception.ParametroAccionInvalidoException;
import com.cfp.mapa.mapper.AuditoriaMapper;
import com.cfp.mapa.model.AuditoriaUsuario;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.repository.AuditoriaRepository;
import com.cfp.mapa.service.AuditoriaService;
import com.cfp.mapa.util.SecurityValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

  private final AuditoriaRepository auditoriaRepository;
  private final AuditoriaMapper auditoriaMapper;
  private final SecurityValidator securityValidator;

  @Transactional(readOnly = true)
  @Override
  public Page<AuditoriaResponseDTO> listarHistorialPaginado(Long usuarioId, String accionStr, int page, int size, String order) {

    securityValidator.validarUsuarioActivoYRoles(Rol.OWNER);

    // Validar si la acción enviada corresponde a una descripción del Enum
    String accionDescripcion = null;

    if (accionStr != null && !accionStr.isBlank()) {
      try {
        accionDescripcion = TipoAccionAuditoria.valueOf(accionStr.toUpperCase().trim()).getDescripcion();
      } catch (IllegalArgumentException e) {
        throw new ParametroAccionInvalidoException("La acción de auditoría proporcionada no es válida");
      }
    }

    Sort orden = order.equalsIgnoreCase("asc")
        ? Sort.by("fechaAccion").ascending()
        : Sort.by("fechaAccion").descending();

    Pageable pageable = PageRequest.of(page, size, orden);

    Page<AuditoriaUsuario> auditorias = auditoriaRepository.buscarConFiltrosDinamicos(usuarioId, accionDescripcion, pageable);

    return auditorias.map(auditoriaMapper::toDTO);
  }

  @Transactional
  @Override
  public void registrarAccion(Usuario operador, Usuario afectado, TipoAccionAuditoria accion) {

    AuditoriaUsuario nuevaAuditoria = new AuditoriaUsuario(operador, afectado, accion.getDescripcion());
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

    // Recorrer y reemplazar los datos personales en base al ID
    for (AuditoriaUsuario auditoria : historialAsociado) {

      if (usuarioId.equals(auditoria.getOperadorId())) {
        auditoria.cambiarDatosOperadorAnonimo(nombreApellidoAnonimo, dniAnonimo);
      }

      if (usuarioId.equals(auditoria.getUsuarioAfectadoId())) {
        auditoria.cambiarDatosAfectadoAnonimo(nombreApellidoAnonimo, dniAnonimo);
      }
    }

    auditoriaRepository.saveAll(historialAsociado);
  }
}