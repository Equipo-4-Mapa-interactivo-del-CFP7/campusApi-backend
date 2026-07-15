package com.cfp.mapa.dto.auditoria;

import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.model.enums.TipoReporte;
import java.time.LocalDateTime;

public record AuditoriaResponseDTO(
    Long id,
    LocalDateTime fechaAccion,
    Long operadorId,
    String operadorNombre,
    String operadorDni,
    String operadorRol,
    Long afectadoId,
    String afectadoNombre,
    String afectadoDni,
    Long reporteId,
    Long reporteEspacioId,
    TipoReporte reporteTipo,
    TipoAccionAuditoria accion,
    Object detalles
) {}
