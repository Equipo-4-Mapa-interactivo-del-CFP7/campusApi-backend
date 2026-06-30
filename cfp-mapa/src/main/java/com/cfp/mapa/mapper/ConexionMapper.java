package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.conexion.ConexionResponseDTO;
import com.cfp.mapa.model.Conexion;
import org.springframework.stereotype.Component;

@Component
public class ConexionMapper {

    public ConexionResponseDTO conexionToResponse(Conexion conexion) {

        return new ConexionResponseDTO(

                conexion.getId(),
                conexion.getOrigen().getId(),
                conexion.getOrigen().getNombre(),
                conexion.getDestino().getId(),
                conexion.getDestino().getNombre(),
                conexion.getTipoTransito(),
                conexion.getDistancia(),
                conexion.getAncho(),
                conexion.getCumpleLey962(),
                conexion.getAccesible(),
                conexion.getActiva()
        );
    }
}
