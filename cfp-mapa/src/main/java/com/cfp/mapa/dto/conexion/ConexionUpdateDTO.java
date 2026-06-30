package com.cfp.mapa.dto.conexion;

import com.cfp.mapa.model.enums.TipoTransito;

public record ConexionUpdateDTO(

        TipoTransito tipoTransito,
        Double distancia,
        Double ancho,
        Boolean cumpleLey962,
        Boolean accesible

) {}
