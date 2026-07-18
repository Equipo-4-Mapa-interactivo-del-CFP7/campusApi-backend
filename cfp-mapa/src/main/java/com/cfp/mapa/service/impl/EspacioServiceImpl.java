package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.espacio.*;
import com.cfp.mapa.exception.EspacioNotFoundException;
import com.cfp.mapa.mapper.EspacioMapper;
import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.enums.EstadoEspacio;
import com.cfp.mapa.model.enums.TipoEspacio;
import com.cfp.mapa.repository.EspacioRepository;
import com.cfp.mapa.service.EspacioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EspacioServiceImpl implements EspacioService {

    private final EspacioRepository espacioRepository;
    private final EspacioMapper espacioMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<EspacioResponseDTO> listarEspacios(
            String nombre,
            String descripcion,
            TipoEspacio tipo,
            Boolean accesible,
            EstadoEspacio estado,
            Pageable pageable
    ) {
        Page<Espacio> espaciosPage = espacioRepository.buscarEspacios(nombre, descripcion, tipo, accesible, estado, pageable);
        return espaciosPage.map(espacioMapper::espacioToResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public EspacioDetalleDTO obtenerEspacioPorId(Long id) {
        Espacio espacio = espacioRepository.findById(id).orElseThrow(() -> new EspacioNotFoundException(id));
        return espacioMapper.espacioToDetalleDTO(espacio);
    }

    @Transactional
    @Override
    public void cambiarEstado(Long id, EstadoEspacio estado) {

        Espacio espacio = espacioRepository.findById(id).orElseThrow(() ->
                new EspacioNotFoundException(id));

        espacio.setEstado(estado);
        espacioRepository.save(espacio);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EspacioMapaDTO> obtenerMapa(TipoEspacio tipo) {
        List<Espacio> espacios;

        if (tipo != null) {
            espacios = espacioRepository.findByEstadoAndTipo(EstadoEspacio.ACTIVO, tipo);
        } else {
            espacios = espacioRepository.findByEstado(EstadoEspacio.ACTIVO);
        }

        return espacios.stream()
                .map(espacioMapper::espacioToMapaDTO)
                .toList();
    }

    @Transactional
    @Override
    public EspacioResponseDTO actualizarEspacio(Long id, EspacioUpdateDTO dto) {

        Espacio espacio = espacioRepository.findById(id).orElseThrow(() -> new EspacioNotFoundException(id));
        espacioMapper.updateToEspacio(dto, espacio);
        Espacio espacioActualizado = espacioRepository.save(espacio);
        return espacioMapper.espacioToResponse(espacioActualizado);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EspacioMapaDTO> buscarEspacios(String nombre) {
        List<Espacio> espacios =
                espacioRepository.findByNombreContainingIgnoreCaseAndEstado(nombre, EstadoEspacio.ACTIVO);

        return espacios.stream()
                .map(espacioMapper::espacioToMapaDTO)
                .toList();
    }
}