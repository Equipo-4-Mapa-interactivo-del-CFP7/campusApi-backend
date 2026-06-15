package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.espacio.*;
import com.cfp.mapa.exception.EspacioNotFoundException;
import com.cfp.mapa.mapper.EspacioMapper;
import com.cfp.mapa.model.Espacio;
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
            Boolean activo,
            Pageable pageable
    ) {
        Page<Espacio> espaciosPage = espacioRepository.buscarEspacios(nombre, descripcion, tipo, accesible, activo, pageable);
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
    public void desactivarEspacio(Long id) {
        Espacio espacio = espacioRepository.findById(id).orElseThrow(() -> new EspacioNotFoundException(id));
        espacio.setActivo(false);
        espacioRepository.save(espacio);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EspacioMapaDTO> obtenerMapa(TipoEspacio tipo) {
        List<Espacio> espacios;

        if (tipo != null) {
            espacios = espacioRepository.findByActivoTrueAndTipo(tipo);
        } else {
            espacios = espacioRepository.findByActivoTrue();
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

    @Transactional
    @Override
    public void activarEspacio(Long id) {
        Espacio espacio = espacioRepository.findById(id).orElseThrow(() -> new EspacioNotFoundException(id));
        espacio.setActivo(true);
        espacioRepository.save(espacio);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EspacioMapaDTO> buscarEspacios(String nombre) {
        List<Espacio> espacios = espacioRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
        return espacios.stream()
                .map(espacioMapper::espacioToMapaDTO)
                .toList();
    }
}