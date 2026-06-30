package com.cfp.mapa.dto.recorrido;

import com.cfp.mapa.dto.conexion.ConexionResponseDTO;
import com.cfp.mapa.dto.espacio.EspacioMapaDTO;

import java.util.List;

public record RutaResponseDTO (

        List<EspacioMapaDTO> recorrido,
        List<ConexionResponseDTO> conexiones,
        Double distanciaTotal
){ }
