package com.cfp.mapa.service.impl;

import com.cfp.mapa.exception.ResourceNotFoundException;
import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.repository.EspacioRepository;
import com.cfp.mapa.service.EspacioService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EspacioServiceImpl implements EspacioService {

    private final EspacioRepository espacioRepository;

    public EspacioServiceImpl(EspacioRepository espacioRepository) {
        this.espacioRepository = espacioRepository;
    }

    @Override
    public List<Espacio> listarTodos() {
        return espacioRepository.findByActivoTrue();
    }

    @Override
    public Espacio obtenerPorId(Long id) {
        return espacioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espacio no encontrado con id: " + id));
    }

    @Override
    public List<Espacio> buscarPorNombre(String nombre) {
        return espacioRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    @Override
    public List<Espacio> listarPorTipo(Espacio.TipoEspacio tipo) {
        return espacioRepository.findByTipoAndActivoTrue(tipo);
    }

    @Override
    public List<Espacio> listarAccesibles() {
        return espacioRepository.findByAccesibleTrueAndActivoTrue();
    }

    @Override
    public Espacio crear(Espacio espacio) {
        return espacioRepository.save(espacio);
    }

    @Override
    public Espacio actualizar(Long id, Espacio datos) {
        Espacio espacio = obtenerPorId(id);
        espacio.setNombre(datos.getNombre());
        espacio.setDescripcion(datos.getDescripcion());
        espacio.setTipo(datos.getTipo());
        espacio.setPiso(datos.getPiso());
        espacio.setCoordenadaX(datos.getCoordenadaX());
        espacio.setCoordenadaY(datos.getCoordenadaY());
        espacio.setAccesible(datos.getAccesible());
        espacio.setFotos(datos.getFotos());
        return espacioRepository.save(espacio);
    }

    @Override
    public void eliminar(Long id) {
        Espacio espacio = obtenerPorId(id);
        espacio.setActivo(false);
        espacioRepository.save(espacio);
    }
}
