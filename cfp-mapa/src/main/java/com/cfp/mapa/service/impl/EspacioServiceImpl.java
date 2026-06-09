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

        Page<Espacio> espaciosPage = espacioRepository.buscarEspacios(
                nombre, descripcion, tipo, accesible, activo, pageable
        );

        return espaciosPage.map(espacioMapper::espacioToResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public EspacioDetalleDTO obtenerEspacioPorId(Long id) {

        Espacio espacio = espacioRepository.findById(id)
                .orElseThrow(() -> new EspacioNotFoundException(id));

        return espacioMapper.espacioToDetalleDTO(espacio);
    }

    @Transactional
    @Override
    public void desactivarEspacio(Long id) {

        Espacio espacio = espacioRepository.findById(id)
                .orElseThrow(() -> new EspacioNotFoundException(id));

        espacio.setActivo(false);

        espacioRepository.save(espacio);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EspacioMapaDTO> obtenerMapa(
            TipoEspacio tipo
    ) {

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
    public EspacioResponseDTO crearEspacio(EspacioRequestDTO dto) {
        return null;
    }

    @Transactional
    @Override
    public EspacioResponseDTO actualizarEspacio(
            Long id,
            EspacioUpdateDTO dto
    ) {
        return null;
    }

    @Transactional
    @Override
    public void activarEspacio(Long id) {

    }

    @Transactional
    @Override
    public void eliminarEspacio(Long id) {

    }



//    private final EspacioRepository espacioRepository;
//
//    public EspacioServiceImpl(EspacioRepository espacioRepository) {
//        this.espacioRepository = espacioRepository;
//    }
//
//    @Override
//    public List<Espacio> listarTodos() {
//        return espacioRepository.findByActivoTrue();
//    }
//
//    @Override
//    public Espacio obtenerPorId(Long id) {
//        return espacioRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Espacio no encontrado con id: " + id));
//    }
//
//    @Override
//    public List<Espacio> buscarPorNombre(String nombre) {
//        return espacioRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
//    }
//
//    @Override
//    public List<Espacio> listarPorTipo(TipoEspacio tipo) {
//        return espacioRepository.findByTipoAndActivoTrue(tipo);
//    }
//
//    @Override
//    public List<Espacio> listarAccesibles() {
//        return espacioRepository.findByAccesibleTrueAndActivoTrue();
//    }
//
//    @Override
//    public Espacio crear(Espacio espacio) {
//        return espacioRepository.save(espacio);
//    }
//
//    @Override
//    public Espacio actualizar(Long id, Espacio datos) {
//        Espacio espacio = obtenerPorId(id);
//        espacio.setNombre(datos.getNombre());
//        espacio.setDescripcion(datos.getDescripcion());
//        espacio.setTipo(datos.getTipo());
//        espacio.setPiso(datos.getPiso());
//        espacio.setCoordenadaX(datos.getCoordenadaX());
//        espacio.setCoordenadaY(datos.getCoordenadaY());
//        espacio.setAccesible(datos.getAccesible());
//        espacio.setFotos(datos.getFotos());
//        return espacioRepository.save(espacio);
//    }
//
//    @Override
//    public void eliminar(Long id) {
//        Espacio espacio = obtenerPorId(id);
//        espacio.setActivo(false);
//        espacioRepository.save(espacio);
//    }
}
