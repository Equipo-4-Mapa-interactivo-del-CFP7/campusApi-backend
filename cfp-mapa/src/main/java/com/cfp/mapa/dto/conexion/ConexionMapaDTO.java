package com.cfp.mapa.dto.conexion;

import com.cfp.mapa.model.enums.TipoTransito;

public record ConexionMapaDTO(

        Long origenId,
        Long destinoId,
        TipoTransito tipoTransito,
        Boolean accesible

){}
