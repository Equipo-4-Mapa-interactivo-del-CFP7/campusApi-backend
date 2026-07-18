package com.cfp.mapa.dto.espacio;

import com.cfp.mapa.model.enums.Sector;
import com.cfp.mapa.model.enums.TipoEspacio;

public record EspacioUpdateDTO (
        String nombre,
        String descripcion,
        TipoEspacio tipo,
        Sector sector,
        Double coordenadaX,
        Double coordenadaY,
        Boolean accesible
){}
