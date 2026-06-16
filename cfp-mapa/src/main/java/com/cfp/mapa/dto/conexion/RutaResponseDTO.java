package com.cfp.mapa.dto.conexion;

import com.cfp.mapa.dto.espacio.EspacioMapaDTO;

import java.util.List;

public record RutaResponseDTO (

        List<EspacioMapaDTO> recorrido,
        Double distanciaTotal
){}
