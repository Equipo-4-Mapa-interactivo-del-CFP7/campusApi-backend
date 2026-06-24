package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.model.AuditoriaUsuario;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

  // AuditoriaUsuario -> AuditoriaResponseDTO
  public AuditoriaResponseDTO toDTO(AuditoriaUsuario auditoria) {

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
        auditoria.getAccion()
    );
  }

}
