package com.cfp.mapa.dto.conexion;

import com.cfp.mapa.model.enums.EstadoConexion;
import com.cfp.mapa.model.enums.TipoTransito;

public record ConexionResponseDTO(

        Long id,
        Long origenId,
        String origenNombre,
        Long destinoId,
        String destinoNombre,
        TipoTransito tipoTransito,
        Double distancia,
        Double ancho,
        Boolean cumpleLey962,
        Boolean accesible,
        EstadoConexion estado
) {}