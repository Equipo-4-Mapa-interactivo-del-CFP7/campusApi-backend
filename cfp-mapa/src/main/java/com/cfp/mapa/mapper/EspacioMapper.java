package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.espacio.EspacioDetalleDTO;
import com.cfp.mapa.dto.espacio.EspacioMapaDTO;
import com.cfp.mapa.dto.espacio.EspacioRequestDTO;
import com.cfp.mapa.dto.espacio.EspacioResponseDTO;
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
                espacio.getAccesible(),
                espacio.getActivo()
        );
    }

    public EspacioMapaDTO espacioToMapaDTO(Espacio espacio) {

        return new EspacioMapaDTO(
                espacio.getId(),
                espacio.getNombre(),
                espacio.getTipo(),
                espacio.getCoordenadaX(),
                espacio.getCoordenadaY(),
                espacio.getAccesible()
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
                null
        );
    }

    public Espacio requestToEspacio(EspacioRequestDTO dto) {

        Espacio espacio = new Espacio();

        espacio.setNombre(dto.nombre());
        espacio.setDescripcion(dto.descripcion());
        espacio.setTipo(dto.tipo());
        espacio.setCoordenadaX(dto.coordenadaX());
        espacio.setCoordenadaY(dto.coordenadaY());
        espacio.setAccesible(dto.accesible());

        return espacio;
    }
}
