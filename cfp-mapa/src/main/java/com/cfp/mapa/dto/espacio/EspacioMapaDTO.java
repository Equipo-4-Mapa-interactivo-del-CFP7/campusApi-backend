package com.cfp.mapa.dto.espacio;

import com.cfp.mapa.model.enums.EstadoEspacio;
import com.cfp.mapa.model.enums.Sector;
import com.cfp.mapa.model.enums.TipoEspacio;

public record EspacioMapaDTO (

        Long id,
        String nombre,
        TipoEspacio tipo,
        Sector sector,
        Double coordenadaX,
        Double coordenadaY,
        Boolean accesible,
        EstadoEspacio estado
){ }
