package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.model.AuditoriaUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Component
public class AuditoriaMapper {

  private final ObjectMapper objectMapper;

  // AuditoriaUsuario -> AuditoriaResponseDTO
  public AuditoriaResponseDTO toDTO(AuditoriaUsuario auditoria) {

    Object detallesMapped = null;
    String detallesRaw = auditoria.getDetalles();

    if (detallesRaw != null && detallesRaw.trim().startsWith("{")) {
      try {
        detallesMapped = objectMapper.readTree(detallesRaw);
      } catch (Exception _) {
      }
    } else { // Si es texto plano, guardarlo
      detallesMapped = detallesRaw;
    }

    return new AuditoriaResponseDTO(
        auditoria.getId(),
        auditoria.getFechaAccion(),
        auditoria.getOperadorId(),
        auditoria.getOperadorNombreCompleto(),
        auditoria.getOperadorDni(),
        auditoria.getOperadorRol(),
        auditoria.getUsuarioAfectadoId(),
        auditoria.getUsuarioAfectadoNombreCompleto(),
        auditoria.getUsuarioAfectadoDni(),
        auditoria.getReporteId(),
        auditoria.getAccion(),
        detallesMapped
    );
  }

}
