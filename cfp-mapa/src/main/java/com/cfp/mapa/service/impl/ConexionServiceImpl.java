package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.conexion.ConexionDTO;
import com.cfp.mapa.dto.conexion.RutaResponseDTO;
import com.cfp.mapa.service.ConexionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ConexionServiceImpl implements ConexionService {

    @Override
    public List<ConexionDTO> obtenerConexiones() {

        // TODO: Lógica
        return null;
    }

    @Override
    public RutaResponseDTO calcularRuta(Long origenId, Long destinoId,Boolean soloAccesible) {

        // TODO: Lógica del recorrido
        return null;
    }
}
