package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.conexion.ConexionDTO;
import com.cfp.mapa.model.Conexion;
import org.springframework.stereotype.Component;

@Component
public class ConexionMapper {

    public ConexionDTO conexionToDTO(Conexion conexion) {

        return new ConexionDTO(
                conexion.getId(),
                conexion.getOrigen().getId(),
                conexion.getOrigen().getNombre(),
                conexion.getDestino().getId(),
                conexion.getDestino().getNombre(),
                conexion.getTipoTransito(),
                conexion.getDistancia(),
                conexion.getAccesible(),
                conexion.getActiva()
        );
    }
}
