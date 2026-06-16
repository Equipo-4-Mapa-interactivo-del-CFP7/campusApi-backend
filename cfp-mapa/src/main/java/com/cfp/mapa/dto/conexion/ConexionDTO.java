package com.cfp.mapa.dto.conexion;

import com.cfp.mapa.model.enums.TipoTransito;

public record ConexionDTO (

        Long id,
        Long origenId,
        String origenNombre,
        Long destinoId,
        String destinoNombre,
        TipoTransito tipoTransito,
        Double distancia,
        Boolean accesible,
        Boolean activa
) { }
