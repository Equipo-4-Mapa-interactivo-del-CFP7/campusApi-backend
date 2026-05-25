package com.cfp.mapa.service.impl;

import com.cfp.mapa.exception.ResourceNotFoundException;
import com.cfp.mapa.model.EstadoReporte;
import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.repository.ReporteRepository;
import com.cfp.mapa.service.ReporteService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteServiceImpl(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    @Override
    public List<Reporte> listarTodos() {
        return reporteRepository.findAll();
    }

    @Override
    public List<Reporte> listarPorEspacio(Long espacioId) {
        return reporteRepository.findByEspacioId(espacioId);
    }

    @Override
    public List<Reporte> listarPorEstado(EstadoReporte estado) {
        return reporteRepository.findByEstado(estado);
    }

    @Override
    public Reporte crear(Reporte reporte) {
        return reporteRepository.save(reporte);
    }

    @Override
    public Reporte actualizarEstado(Long id, EstadoReporte nuevoEstado) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + id));
        reporte.setEstado(nuevoEstado);
        return reporteRepository.save(reporte);
    }
}
