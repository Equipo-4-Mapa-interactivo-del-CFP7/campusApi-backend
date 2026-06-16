package com.cfp.mapa.service;

import com.cfp.mapa.dto.conexion.ConexionDTO;
import com.cfp.mapa.dto.conexion.RutaResponseDTO;

import java.util.List;

public interface ConexionService {

    List<ConexionDTO> obtenerConexiones();

    RutaResponseDTO calcularRuta(
            Long origenId,
            Long destinoId,
            Boolean soloAccesible
    );
}
