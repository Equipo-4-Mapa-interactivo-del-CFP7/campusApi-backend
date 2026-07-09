package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.auditoria.AuditoriaReporteCreadoDTO;
import com.cfp.mapa.dto.auditoria.AuditoriaReportesDetallesDTO;
import com.cfp.mapa.dto.reporte.ReporteCreateRequestDTO;
import com.cfp.mapa.dto.reporte.ReporteResponseDTO;
import com.cfp.mapa.dto.reporte.ReporteUpdateRequestDTO;
import com.cfp.mapa.exception.AccionInvalidaException;
import com.cfp.mapa.exception.DniNotFoundException;
import com.cfp.mapa.exception.EspacioNotFoundException;
import com.cfp.mapa.exception.OperacionInvalidaException;
import com.cfp.mapa.exception.ReporteConflictException;
import com.cfp.mapa.exception.ReporteNotFoundException;
import com.cfp.mapa.exception.SolicitudIncorrectaException;
import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.model.enums.TipoReporte;
import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.mapper.ReporteMapper;
import com.cfp.mapa.repository.EspacioRepository;
import com.cfp.mapa.repository.ReporteRepository;
import com.cfp.mapa.repository.UsuarioRepository;
import com.cfp.mapa.service.AuditoriaService;
import com.cfp.mapa.service.ReporteService;
import com.cfp.mapa.util.SecurityUtils;
import com.cfp.mapa.util.SecurityValidator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    private final SecurityValidator securityValidator;
    private final SecurityUtils securityUtils;
    private final AuditoriaService auditoriaService;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    @Override
    public ReporteResponseDTO crearReporte(ReporteCreateRequestDTO request) {

        securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL);

        // Busca si ya existe el mismo tipo de reporte
        TipoReporte tipoReporte = reporteMapper.strToTipoReporte(request.tipoReporte());

        Espacio espacio = espacioRepository.findById(request.espacioId()).orElseThrow(
            () -> new EspacioNotFoundException(request.espacioId())
        );

        List<EstadoReporte> estadosActivos = List.of(
            EstadoReporte.PENDIENTE, EstadoReporte.EN_REVISION
            );

        boolean existe = reporteRepository.existsByEspacioAndTipoAndEstadoIn(
            espacio, tipoReporte, estadosActivos
        );

        if (existe) {
            throw new ReporteConflictException(request.tipoReporte(), espacio.getNombre());
        }

        // Si el tipo de reporte es "OTROS" debe tener una descripcion
        if (tipoReporte.equals(TipoReporte.OTROS) &&
            (request.descripcion() == null || request.descripcion().isBlank())
        ) {

            throw new OperacionInvalidaException(
                "No se puede crear un reporte del tipo 'OTROS' sin descripción"
            );
        }

        // Si no existe, crea un nuevo reporte
        Reporte reporte = reporteMapper.createToReporte(request, espacio, tipoReporte);

        // Si es creado con temporizador se considera atendido
        Usuario usuarioLogueado = securityUtils.usuarioLogueado();

        if (request.minutosEstimados() != null) {

            reporte.setEstado(EstadoReporte.EN_REVISION);
            reporte.setAtendidoPor(usuarioLogueado);
            reporte.setFechaAtencion(LocalDateTime.now());
        }

        Reporte reporteGuardado = reporteRepository.save(reporte);

        AuditoriaReporteCreadoDTO detallesDto = null;

        if (request.minutosEstimados() != null) {
            detallesDto = new AuditoriaReporteCreadoDTO(
                request.minutosEstimados(),
                LocalDateTime.now().minusMinutes(request.minutosEstimados())
            );
        }

        auditoriaService.registrarAccion(
            usuarioLogueado,
            null,
            reporteGuardado.getId(),
            TipoAccionAuditoria.REPORTE_CREADO,
            detallesDto
        );

        return reporteMapper.ReporteToResponse(reporteGuardado);
    }

    @Transactional
    @Override
    public ReporteResponseDTO atenderReporte(Long id, ReporteUpdateRequestDTO request) {

        securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL);

        if (request.minutosEstimados() != null && request.quitarContador()) {
            throw new SolicitudIncorrectaException(
                "No se puede quitar el contador y agregar minutos en la misma solicitud"
            );
        }

        Reporte reporte = reporteRepository.findById(id).orElseThrow(
            () -> new ReporteNotFoundException(id)
        );

        if (!reporte.getEstado().equals(EstadoReporte.PENDIENTE) &&
            !reporte.getEstado().equals(EstadoReporte.EN_REVISION)) {
            throw new AccionInvalidaException("Solo se pueden modificar reportes pendientes o en revisión");
        }

        if (reporte.getFechaVencimiento() == null &&
            request.quitarContador()
        ) {

            throw new OperacionInvalidaException("No puedes quitarle el contador a un reporte sin contador");
        }

        boolean eraPendiente = reporte.getEstado().equals(EstadoReporte.PENDIENTE);

        // Si no cambia nada
        if (!eraPendiente &&
            request.minutosEstimados() == null &&
            !request.quitarContador() &&
            (request.descripcion() == null || Objects.equals(request.descripcion(), reporte.getDescripcion()))
        ) {

            throw new SolicitudIncorrectaException("No has realizado ningún cambio en el reporte");
        }

        Usuario usuarioLogueado =  securityUtils.usuarioLogueado();
        TipoAccionAuditoria tipoAccionAuditoria = null;

        // Variables para el AuditoriaReportesDetallesDTO
        boolean crearDTO = false;
        Integer minutosEstimados = null;
        String descripcionAnterior = null;
        String descripcionNueva = null;
        LocalDateTime fechaVencimientoAnterior = null;
        LocalDateTime fechaVencimientoNueva = null;

        if (eraPendiente) {
            reporte.setEstado(EstadoReporte.EN_REVISION);
            reporte.setFechaAtencion(LocalDateTime.now());
            reporte.setAtendidoPor(usuarioLogueado);

            tipoAccionAuditoria = TipoAccionAuditoria.REPORTE_ATENDIDO;
        }

        // Se modifica el tiempo restante
        if (request.minutosEstimados() != null) {

            minutosEstimados = request.minutosEstimados();
            fechaVencimientoAnterior = reporte.getFechaVencimiento();
            fechaVencimientoNueva = LocalDateTime.now().plusMinutes(request.minutosEstimados());
            crearDTO = true;

            reporte.setAtendidoPor(usuarioLogueado);
            reporte.setMinutosEstimados(request.minutosEstimados());
            reporte.setFechaVencimiento(fechaVencimientoNueva);

            if (!eraPendiente) {
                tipoAccionAuditoria = TipoAccionAuditoria.REPORTE_MODIFICADO;
            }
        }

        // Se quita el tiempo
        if (request.quitarContador()) {

            fechaVencimientoAnterior = reporte.getFechaVencimiento();
            crearDTO = true;

            reporte.setAtendidoPor(usuarioLogueado);
            reporte.setMinutosEstimados(null);
            reporte.setFechaVencimiento(null);

            if (!eraPendiente) {
                tipoAccionAuditoria = TipoAccionAuditoria.REPORTE_TIEMPO_ELIMINADO;
            }
        }

        // Se cambia la descripcion (solo si es distinta)
        if (request.descripcion() != null &&
            !request.descripcion().equals(reporte.getDescripcion())
        ) {

            descripcionAnterior = reporte.getDescripcion();
            descripcionNueva = request.descripcion();
            crearDTO = true;

            reporte.setDescripcion(descripcionNueva);

            if (!eraPendiente) {
                tipoAccionAuditoria = TipoAccionAuditoria.REPORTE_MODIFICADO;
            }
        }

        Reporte reporteGuardado = reporteRepository.save(reporte);

        // Crear AuditoriaReportesDetallesDTO
        AuditoriaReportesDetallesDTO detallesDTO = null;

        if (crearDTO) {
            detallesDTO = new AuditoriaReportesDetallesDTO(
                minutosEstimados,
                descripcionAnterior,
                descripcionNueva,
                fechaVencimientoAnterior,
                fechaVencimientoNueva
            );
        }

        auditoriaService.registrarAccion(
            usuarioLogueado,
            null,
            reporteGuardado.getId(),
            tipoAccionAuditoria,
            detallesDTO
        );

        return reporteMapper.ReporteToResponse(reporteGuardado);
    }

    @Transactional
    @Override
    public ReporteResponseDTO resolverReporte(Long id) {

        securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL);

        Reporte reporte = reporteRepository.findById(id).orElseThrow(
            () -> new ReporteNotFoundException(id)
        );

        if (reporte.getEstado().equals(EstadoReporte.RESUELTO)) {
            throw new SolicitudIncorrectaException("El reporte ya se encuentra resuelto");
        }

        Usuario usuarioLogueado = securityUtils.usuarioLogueado();

        if (reporte.getEstado().equals(EstadoReporte.PENDIENTE)) {
            reporte.setFechaAtencion(LocalDateTime.now());
        }

        reporte.setEstado(EstadoReporte.RESUELTO);
        reporte.setMinutosEstimados(null);
        reporte.setFechaVencimiento(null);
        reporte.setAtendidoPor(usuarioLogueado);

        Reporte reporteGuardado = reporteRepository.save(reporte);

        auditoriaService.registrarAccion(
            usuarioLogueado,
            null,
            reporteGuardado.getId(),
            TipoAccionAuditoria.REPORTE_CERRADO,
            null
        );

        return reporteMapper.ReporteToResponse(reporteGuardado);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ReporteResponseDTO> listarReporteConFiltro(
        Long espacioId,
        String estado,
        String tipoReporte,
        Pageable pageable
    ) {

        securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL);

        EstadoReporte estadoParam = (estado != null && !estado.isBlank()) ?
            EstadoReporte.valueOf(estado.toUpperCase().trim()) : null;

        TipoReporte tipoParam = (tipoReporte != null && !tipoReporte.isBlank()) ?
            TipoReporte.valueOf(tipoReporte.toUpperCase().trim()) : null;

        Page<Reporte> reportesPage = reporteRepository.buscarReportesDinamico(
            espacioId, estadoParam, tipoParam, pageable
        );

        return reportesPage.map(reporteMapper::ReporteToResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public ReporteResponseDTO obtenerReporte(Long id) {

        securityValidator.validarUsuarioActivoYRoles(Rol.OWNER, Rol.ADMIN, Rol.PERSONAL);

        Reporte reporte = reporteRepository.findById(id).orElseThrow(
            () -> new ReporteNotFoundException(id)
        );

        return reporteMapper.ReporteToResponse(reporte);
    }

    @Transactional
    @Override
    public void cerrarReportesAutomaticamente() {

        List<EstadoReporte> estadosActivos = List.of(
            EstadoReporte.PENDIENTE, EstadoReporte.EN_REVISION
        );

        List<Reporte> reportes = reporteRepository.findByEstadoInAndFechaVencimientoLessThanEqual(
            estadosActivos,
            LocalDateTime.now()
        );

        // Si se encuentran reportes vencidos, se procede a resolverlos
        if (!reportes.isEmpty()) {

            Usuario system = usuarioRepository.findByDni("SYSTEM01").orElseThrow(
                () -> new DniNotFoundException("SYSTEM01")
            );

            for (Reporte reporte : reportes) {

                reporte.setMinutosEstimados(null);
                reporte.setFechaVencimiento(null);
                reporte.setAtendidoPor(system);
                reporte.setEstado(EstadoReporte.RESUELTO);

                auditoriaService.registrarAccion(
                    system,
                    null,
                    reporte.getId(),
                    TipoAccionAuditoria.REPORTE_CERRADO_AUTOMATICO,
                    null
                );
            }

            // Guardar en una sola consulta
            reporteRepository.saveAll(reportes);
        }
    }

}
