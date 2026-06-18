package com.cfp.mapa.dto.auditoria;

import java.time.LocalDateTime;

public record AuditoriaResponseDTO(
    Long id,
    LocalDateTime fechaAccion,
    String operadorNombre,
    String afectadoNombre,
    String accion
) {}
