package com.cfp.mapa.dto.espacio;

import com.cfp.mapa.dto.imagen.ImagenDTO;
import com.cfp.mapa.model.enums.TipoEspacio;

import java.util.List;

public record EspacioDetalleDTO (

        Long id,
        String nombre,
        String descripcion,
        TipoEspacio tipo,
        Double coordenadaX,
        Double coordenadaY,
        Boolean accesible,
        List<ImagenDTO> imagenes
){
}
