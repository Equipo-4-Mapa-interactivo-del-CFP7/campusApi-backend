package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.dto.reporte.ReporteUpdateRequestDTO;
import com.cfp.mapa.exception.ResourceNotFoundException;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.mapper.ReporteMapper;
import com.cfp.mapa.repository.ReporteRepository;
import com.cfp.mapa.service.ReporteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final ReporteMapper reporteMapper;

    public ReporteServiceImpl(ReporteRepository reporteRepository, ReporteMapper reporteMapper) {
        this.reporteRepository = reporteRepository;
        this.reporteMapper = reporteMapper;
    }

//    @Override
//    public List<Reporte> listarTodos() {
//        return reporteRepository.findAll();
//    }
//
//    @Override
//    public List<Reporte> listarPorEspacio(Long espacioId) {
//        return reporteRepository.findByEspacioId(espacioId);
//    }
//
//    @Override
//    public List<Reporte> listarPorEstado(EstadoReporte estado) {
//        return reporteRepository.findByEstado(estado);
//    }
//
    @Override
    public ReporteResponseDTO crearReporte(ReporteCreateRequestDTO request, MultipartFile foto) {
        Reporte reporteGuardado = reporteMapper.createToReporte(request);
        reporteRepository.save(reporteGuardado);
        return reporteMapper.ReporteToResponse(reporteGuardado);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ReporteResponseDTO> listarReporteConFiltro(
            Long id,
            EstadoReporte estado,
            TipoReporte tipoReporte,
            Pageable pageable
    ) {

        Page<Reporte> reportesPage = reporteRepository.buscarReportesDinamico(
                id, estado, tipoReporte, pageable
        );

        return reportesPage.map(reporteMapper::ReporteToResponse);
    }

    @Override
    public ReporteResponseDTO actualizarEstado(Long id, ReporteUpdateRequestDTO request) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + id));
        reporte.setEstado(reporteMapper.strToEstadoReporte(request.estado()));
        reporteRepository.save(reporte);
        return reporteMapper.ReporteToResponse(reporte);
    }
}
