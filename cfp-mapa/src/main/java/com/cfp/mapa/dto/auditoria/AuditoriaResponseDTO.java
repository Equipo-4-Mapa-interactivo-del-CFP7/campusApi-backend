package com.cfp.mapa.dto.auditoria;

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
    String accion
) {}
