package com.cfp.mapa.service;

import com.cfp.mapa.dto.recorrido.RutaResponseDTO;

public interface RecorridoService {
    RutaResponseDTO calcularRuta(
            Long origenId,
            Long destinoId,
            Boolean soloAccesible
    );
}
