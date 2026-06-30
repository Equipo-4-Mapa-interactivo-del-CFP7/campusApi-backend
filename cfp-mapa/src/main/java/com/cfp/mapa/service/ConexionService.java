package com.cfp.mapa.service;

import com.cfp.mapa.dto.conexion.ConexionResponseDTO;
import com.cfp.mapa.dto.conexion.ConexionUpdateDTO;

import java.util.List;

public interface ConexionService {

    // ADMIN / CONSULTA

    List<ConexionResponseDTO> listarConexiones();
    ConexionResponseDTO obtenerConexionPorId(Long id);
    ConexionResponseDTO actualizarConexion(Long id, ConexionUpdateDTO dto);

    void desactivarConexion(Long id);

    void activarConexion(Long id);

}