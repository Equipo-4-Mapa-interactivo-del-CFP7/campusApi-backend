package com.cfp.mapa.dto.recorrido;

import com.cfp.mapa.dto.conexion.ConexionMapaDTO;
import com.cfp.mapa.dto.conexion.ConexionResponseDTO;
import com.cfp.mapa.dto.espacio.EspacioMapaDTO;

import java.util.List;

public record RutaResponseDTO (

        List<EspacioMapaDTO> espacios,
        List<ConexionMapaDTO> conexiones,
        Double distanciaTotal
){}
