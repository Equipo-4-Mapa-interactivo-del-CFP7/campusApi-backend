package com.cfp.mapa.service.impl;


import com.cfp.mapa.dto.conexion.ConexionMapaDTO;
import com.cfp.mapa.dto.conexion.ConexionResponseDTO;
import com.cfp.mapa.dto.conexion.ConexionUpdateDTO;
import com.cfp.mapa.exception.ConexionNotFoundException;
import com.cfp.mapa.mapper.ConexionMapper;
import com.cfp.mapa.model.Conexion;
import com.cfp.mapa.model.enums.EstadoConexion;
import com.cfp.mapa.repository.ConexionRepository;
import com.cfp.mapa.service.ConexionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ConexionServiceImpl implements ConexionService {

    private final ConexionRepository conexionRepository;
    private final ConexionMapper conexionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ConexionResponseDTO> listarConexiones() {

        return conexionRepository.findAll()
                .stream()
                .map(conexionMapper::conexionToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ConexionResponseDTO actualizarConexion(Long id, ConexionUpdateDTO dto) {

        Conexion conexion = conexionRepository.findById(id).orElseThrow(() ->
                new ConexionNotFoundException(id));

        conexion.setTipoTransito(dto.tipoTransito());
        conexion.setDistancia(dto.distancia());
        conexion.setAncho(dto.ancho());
        conexion.setCumpleLey962(dto.cumpleLey962());
        conexion.setAccesible(dto.accesible());

        Conexion actualizada = conexionRepository.save(conexion);

        return conexionMapper.conexionToResponse(actualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public ConexionResponseDTO obtenerConexionPorId(Long id) {

        Conexion conexion = conexionRepository.findById(id).orElseThrow(() ->
                new ConexionNotFoundException(id));

        return conexionMapper.conexionToResponse(conexion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConexionMapaDTO> obtenerMapa() {

        return conexionRepository.findByEstadoAndAccesibleTrueOrderByIdAsc(EstadoConexion.ACTIVA)
                .stream()
                .map(conexionMapper::conexionToMapaDTO)
                .toList();
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, EstadoConexion estado) {

        Conexion conexion = conexionRepository.findById(id).orElseThrow(() ->
                new ConexionNotFoundException(id));

        conexion.setEstado(estado);
        conexionRepository.save(conexion);
    }
}
