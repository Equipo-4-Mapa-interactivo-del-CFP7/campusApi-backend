package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.model.AuditoriaUsuario;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

  public AuditoriaResponseDTO toDTO(AuditoriaUsuario auditoria) {

    String operadorNombre = auditoria.getOperador().getNombre() + " " + auditoria.getOperador().getApellido();
    String afectadoNombre = auditoria.getUsuarioAfectado().getNombre() + " " + auditoria.getUsuarioAfectado().getApellido();

    return new AuditoriaResponseDTO(
        auditoria.getId(),
        auditoria.getFechaAccion(),
        auditoria.getOperador().getId(),
        operadorNombre,
        auditoria.getOperadorRol(),
        auditoria.getUsuarioAfectado().getId(),
        afectadoNombre,
        auditoria.getAccion()
    );
  }
}
