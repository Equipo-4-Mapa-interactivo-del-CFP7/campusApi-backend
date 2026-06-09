package com.cfp.mapa.dto.espacio;

import com.cfp.mapa.model.enums.TipoEspacio;

public record EspacioResponseDTO (
        Long id,
        String nombre,
        String descripcion,
        TipoEspacio tipo,
        Boolean accesible,
        Boolean activo
){ }


