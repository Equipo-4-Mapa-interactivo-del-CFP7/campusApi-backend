package com.cfp.mapa.service;

import com.cfp.mapa.model.Espacio;
import java.util.List;

public interface EspacioService {
    List<Espacio> listarTodos();
    Espacio obtenerPorId(Long id);
    List<Espacio> buscarPorNombre(String nombre);
    List<Espacio> listarPorTipo(Espacio.TipoEspacio tipo);
    List<Espacio> listarAccesibles();
    Espacio crear(Espacio espacio);
    Espacio actualizar(Long id, Espacio espacio);
    void eliminar(Long id);
}
