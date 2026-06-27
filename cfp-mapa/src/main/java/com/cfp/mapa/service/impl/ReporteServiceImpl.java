package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.dto.reporte.ReporteUpdateRequestDTO;
import com.cfp.mapa.exception.ResourceNotFoundException;
import com.cfp.mapa.exception.ReporteConflictException;
import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.mapper.ReporteMapper;
import com.cfp.mapa.repository.EspacioRepository;
import com.cfp.mapa.repository.ReporteRepository;
import com.cfp.mapa.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final ReporteMapper reporteMapper;
    private final EspacioRepository espacioRepository;

    @Transactional
    @Override
    public ReporteResponseDTO crearReporte(ReporteCreateRequestDTO request) {

//        Espacio espacio = espacioRepository.findById(request.espacioId()).orElseThrow(
//            () -> new ResourceNotFoundException(
//                "Espacio no encontrado con id: " + request.espacioId())
//        );
        if (!espacioRepository.existsById(request.espacioId())) {
            throw new ResourceNotFoundException(
                    "Espacio no encontrado con id: " + request.espacioId());
        }

        // Busca si ya existe el reporte
        boolean existe = reporteRepository.existByIdEspacioAndTipo(request.espacioId(), request.tipoReporte());
        if(existe) {
            Espacio espacio = espacioRepository.findById(request.espacioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Espacio no encontrado"));
            throw new ReporteConflictException(request.tipoReporte().name(), espacio.getNombre());
        }

        Reporte reporte = reporteMapper.createToReporte(request);
        reporte.setUrlImagen(request.imagenURL());

//        if (foto != null && !foto.isEmpty()) {
//            // TODO: subir la foto y obtener unicamente el link
//            // String url = uploadService.subir(foto);
//            // reporte.setUrlImagen(url);
//        } else {
//            reporte.setUrlImagen(null);
//        }

        Reporte reporteGuardado = reporteRepository.save(reporte);

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

    @Override
    public ReporteResponseDTO actualizarEstado(Long id, ReporteUpdateRequestDTO request) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reporte no encontrado con id: " + id));

        reporte.setEstado(reporteMapper.strToEstadoReporte(request.estado()));
        reporteRepository.save(reporte);

        return reporteMapper.ReporteToResponse(reporte);
    }

}
