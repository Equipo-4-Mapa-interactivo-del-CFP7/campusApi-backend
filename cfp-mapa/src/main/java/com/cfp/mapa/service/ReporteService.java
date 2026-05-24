package com.cfp.mapa.service;

import com.cfp.mapa.model.Reporte;
import java.util.List;

public interface ReporteService {
    List<Reporte> listarTodos();
    List<Reporte> listarPorEspacio(Long espacioId);
    List<Reporte> listarPorEstado(Reporte.EstadoReporte estado);
    Reporte crear(Reporte reporte);
    Reporte actualizarEstado(Long id, Reporte.EstadoReporte nuevoEstado);
}
