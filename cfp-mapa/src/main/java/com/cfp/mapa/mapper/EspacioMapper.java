package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.espacio.*;
import com.cfp.mapa.model.Espacio;
import org.springframework.stereotype.Component;

@Component
public class EspacioMapper {

    public EspacioResponseDTO espacioToResponse(Espacio espacio) {
        return new EspacioResponseDTO(
                espacio.getId(),
                espacio.getNombre(),
                espacio.getDescripcion(),
                espacio.getTipo(),
                espacio.getSector(),
                espacio.getAccesible(),
                espacio.getEstado()
        );
    }

    public EspacioMapaDTO espacioToMapaDTO(Espacio espacio) {

        return new EspacioMapaDTO(
                espacio.getId(),
                espacio.getNombre(),
                espacio.getTipo(),
                espacio.getSector(),
                espacio.getCoordenadaX(),
                espacio.getCoordenadaY(),
                espacio.getAccesible(),
                espacio.getEstado()
        );
    }

    public EspacioDetalleDTO espacioToDetalleDTO(Espacio espacio) {

        return new EspacioDetalleDTO(
                espacio.getId(),
                espacio.getNombre(),
                espacio.getDescripcion(),
                espacio.getTipo(),
                espacio.getCoordenadaX(),
                espacio.getCoordenadaY(),
                espacio.getAccesible(),
                espacio.getEstado(),
                null // imágenes
        );
    }

    public void updateToEspacio(EspacioUpdateDTO dto, Espacio espacio) {

        espacio.setNombre(dto.nombre());
        espacio.setDescripcion(dto.descripcion());
        espacio.setTipo(dto.tipo());
        espacio.setCoordenadaX(dto.coordenadaX());
        espacio.setCoordenadaY(dto.coordenadaY());
        espacio.setAccesible(dto.accesible());
    }
}
