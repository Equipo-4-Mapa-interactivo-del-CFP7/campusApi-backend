package com.cfp.mapa.service;

import com.cfp.mapa.dto.conexion.ConexionMapaDTO;
import com.cfp.mapa.dto.conexion.ConexionResponseDTO;
import com.cfp.mapa.dto.conexion.ConexionUpdateDTO;
import com.cfp.mapa.model.enums.EstadoConexion;

import java.util.List;

public interface ConexionService {

    // ADMIN / CONSULTA

    List<ConexionResponseDTO> listarConexiones();
    ConexionResponseDTO obtenerConexionPorId(Long id);
    ConexionResponseDTO actualizarConexion(Long id, ConexionUpdateDTO dto);

    void cambiarEstado(Long id, EstadoConexion estado);

    List<ConexionMapaDTO> obtenerMapa();
}