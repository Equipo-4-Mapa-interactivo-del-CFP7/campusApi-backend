package com.cfp.mapa.dto.conexion;

import com.cfp.mapa.model.enums.TipoTransito;

public record ConexionRequestDTO (

        Long origenId,
        Long destinoId,
        TipoTransito tipoTransito,
        Double distancia,
        Boolean accesible
) {}
