package com.cfp.mapa.dto.espacio;

import com.cfp.mapa.dto.imagen.ImagenDTO;
import com.cfp.mapa.model.enums.EstadoEspacio;
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
        EstadoEspacio estado,
        List<ImagenDTO> imagenes
){
}
