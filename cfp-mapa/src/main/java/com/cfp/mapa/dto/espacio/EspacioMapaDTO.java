package com.cfp.mapa.dto.espacio;

import com.cfp.mapa.model.enums.TipoEspacio;

public record EspacioMapaDTO (

        Long id,
        String nombre,
        TipoEspacio tipo,
        Double coordenadaX,
        Double coordenadaY,
        Boolean accesible
){
}
