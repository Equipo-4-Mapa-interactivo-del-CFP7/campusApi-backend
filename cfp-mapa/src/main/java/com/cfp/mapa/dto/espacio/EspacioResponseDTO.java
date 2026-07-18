package com.cfp.mapa.dto.espacio;

import com.cfp.mapa.model.enums.EstadoEspacio;
import com.cfp.mapa.model.enums.Sector;
import com.cfp.mapa.model.enums.TipoEspacio;

public record EspacioResponseDTO (
        Long id,
        String nombre,
        String descripcion,
        TipoEspacio tipo,
        Sector sector,
        Boolean accesible,
        EstadoEspacio estado
){ }


