package com.cfp.mapa.service;

import com.cfp.mapa.model.Espacio;
import org.springframework.stereotype.Service;
import com.cfp.mapa.model.enums.TipoEspacio;
import java.util.List;

@Service
public interface EspacioService {
    List<Espacio> listarTodos();
    Espacio obtenerPorId(Long id);
    List<Espacio> buscarPorNombre(String nombre);
    List<Espacio> listarPorTipo(TipoEspacio tipo);
    List<Espacio> listarAccesibles();
    Espacio crear(Espacio espacio);
    Espacio actualizar(Long id, Espacio espacio);
    void eliminar(Long id);
}
