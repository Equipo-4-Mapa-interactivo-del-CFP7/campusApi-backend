package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.metricas.BusquedasMapaDTO;
import com.cfp.mapa.dto.metricas.CuentasDTO;
import com.cfp.mapa.dto.metricas.DatosDTO;
import com.cfp.mapa.dto.metricas.MetricaResponseDTO;
import com.cfp.mapa.dto.metricas.RendimientoDTO;
import com.cfp.mapa.dto.metricas.ReportesDTO;
import com.cfp.mapa.dto.metricas.TopEspaciosDTO;
import com.cfp.mapa.dto.metricas.UsuariosReportesDTO;
import com.cfp.mapa.exception.OperacionInvalidaException;
import com.cfp.mapa.mapper.MetricasMapper;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.repository.AuditoriaRepository;
import com.cfp.mapa.repository.EspacioRepository;
import com.cfp.mapa.repository.RegistroBusquedaRepository;
import com.cfp.mapa.repository.UsuarioRepository;
import com.cfp.mapa.repository.projection.CantidadPorAccionProjection;
import com.cfp.mapa.repository.projection.EdicionDatosProjection;
import com.cfp.mapa.repository.projection.EspacioNombreProjection;
import com.cfp.mapa.repository.projection.RendimientoReporteProjection;
import com.cfp.mapa.repository.projection.TopEspacioBusquedaProjection;
import com.cfp.mapa.repository.projection.TopEspacioProjection;
import com.cfp.mapa.repository.projection.TopRolProjection;
import com.cfp.mapa.repository.projection.TopRutaBusquedaProjection;
import com.cfp.mapa.repository.projection.TopUsuarioProjection;
import com.cfp.mapa.repository.projection.TotalBusquedasProjection;
import com.cfp.mapa.repository.projection.UsuarioNombreProjection;
import com.cfp.mapa.service.MetricasService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MetricasServiceImpl implements MetricasService {

  private final AuditoriaRepository auditoriaRepository;
  private final EspacioRepository espacioRepository;
  private final UsuarioRepository usuarioRepository;
  private final RegistroBusquedaRepository registroBusquedaRepository;
  private final MetricasMapper metricasMapper;

  // Flag en memoria para saber si ya hay un reporte procesandose
  private final AtomicBoolean procesandoReporte = new AtomicBoolean(false);

  @Transactional(readOnly = true, timeout = 10)
  @Override
  public MetricaResponseDTO obtenerMetricaEntreFechas(
      LocalDate desde,
      LocalDate hasta,
      Long topEspaciosCriticos,
      Long topUsuarios,
      Long topRoles,
      Long topBusquedas
  ) {

    if (desde.isAfter(hasta)) {
      throw new OperacionInvalidaException(
          "La fecha de inicio no puede ser posterior a la fecha de fin");
    }

    if (hasta.isAfter(LocalDate.now())) {
      throw new OperacionInvalidaException("La fecha maxima no puede ser superior al día de hoy");
    }

    // Intenta cambiar de false a true. Si falla (devuelve false), significa que ya estaba en true.
    if (!procesandoReporte.compareAndSet(false, true)) {
      throw new OperacionInvalidaException(
          "Ya se están calculando las métricas en este momento. Por favor, aguarde."
      );
    }

    try {

      LocalDateTime desdeDateTime = desde.atStartOfDay();
      LocalDateTime hastaDateTime = hasta.atTime(LocalTime.MAX);

      // Armar la informacion global de auditoria
      List<CantidadPorAccionProjection> conteosGlobales =
          auditoriaRepository.countAccionesEnRango(desdeDateTime, hastaDateTime);

      List<EdicionDatosProjection> conteosEdiciones =
          auditoriaRepository.countEdicionesEnRango(
              desdeDateTime,
              hastaDateTime,
              List.of(
                  TipoAccionAuditoria.DNI_EDITADO,
                  TipoAccionAuditoria.NOMBRE_APELLIDO_EDITADO
              )
          );

      List<RendimientoReporteProjection> rendimientoProyecciones =
          auditoriaRepository.findRendimientoPorTipo(
              desdeDateTime,
              hastaDateTime,
              TipoAccionAuditoria.REPORTE_CREADO,
              List.of(
                  TipoAccionAuditoria.REPORTE_CERRADO,
                  TipoAccionAuditoria.REPORTE_CERRADO_AUTOMATICO
              )
          );

      // Mapear datos globales estaticos
      CuentasDTO cuentasDTO = metricasMapper.mapearCuentas(conteosGlobales);
      DatosDTO datosDTO = metricasMapper.mapearDatos(conteosEdiciones);
      List<RendimientoDTO> listRendimientoDTO = metricasMapper.mapearRendimiento(
          rendimientoProyecciones);

      // Procesar e infraestructura del TOP Espacios Criticos
      List<TopEspaciosDTO> listTopEspaciosDTO = null;
      if (topEspaciosCriticos != null && topEspaciosCriticos > 0) {
        List<TopEspacioProjection> proyeccionesEspacios = auditoriaRepository.findTopEspaciosEnRango(
            desdeDateTime,
            hastaDateTime,
            TipoAccionAuditoria.REPORTE_CREADO
        );

        Map<Long, String> mapaNombresEspacios = Collections.emptyMap();
        if (!proyeccionesEspacios.isEmpty()) {
          Set<Long> espacioIds = proyeccionesEspacios.stream()
              .map(TopEspacioProjection::getEspacioId)
              .collect(Collectors.toSet());

          List<EspacioNombreProjection> nombresProyecciones = espacioRepository.findNombresByIds(
              espacioIds);
          mapaNombresEspacios = nombresProyecciones.stream()
              .collect(Collectors.toMap(EspacioNombreProjection::getId,
                  EspacioNombreProjection::getNombre, (a, b) -> a));
        }

        listTopEspaciosDTO = metricasMapper.mapearTopEspacios(proyeccionesEspacios,
            mapaNombresEspacios, topEspaciosCriticos);
      }

      ReportesDTO reportesDTO = metricasMapper.mapearReportes(conteosGlobales, listRendimientoDTO,
          listTopEspaciosDTO);

      // Procesar e infraestructura del TOP Usuarios y Roles
      Map<TipoAccionAuditoria, List<TopUsuarioProjection>> proyeccionesUsuariosPorAccion = new HashMap<>();
      Map<Long, String> mapaNombresUsuarios = new HashMap<>();
      Map<TipoAccionAuditoria, List<TopRolProjection>> proyeccionesRolesPorAccion = new HashMap<>();

      List<TipoAccionAuditoria> accionesInteres = List.of(
          TipoAccionAuditoria.REPORTE_CREADO,
          TipoAccionAuditoria.REPORTE_ATENDIDO,
          TipoAccionAuditoria.REPORTE_CERRADO
      );

      if (topUsuarios != null && topUsuarios > 0) {
        Pageable limiteUser = PageRequest.of(0, topUsuarios.intValue());
        Set<Long> todosLosUserIds = new HashSet<>();

        for (TipoAccionAuditoria accion : accionesInteres) {
          List<TopUsuarioProjection> topUsers = auditoriaRepository.findTopUsuariosPorAccion(
              desdeDateTime, hastaDateTime, accion, limiteUser);
          proyeccionesUsuariosPorAccion.put(accion, topUsers);
          topUsers.stream().map(TopUsuarioProjection::getUsuarioId).forEach(todosLosUserIds::add);
        }

        if (!todosLosUserIds.isEmpty()) {
          List<UsuarioNombreProjection> nombresUsers = usuarioRepository.findNombresByIds(
              todosLosUserIds);

          mapaNombresUsuarios = nombresUsers.stream()
              .filter(u -> u.getId() != null)
              .collect(Collectors.toMap(
                  UsuarioNombreProjection::getId,
                  u -> u.getNombre() != null ? u.getNombre() : "Sin Nombre",
                  (existente, nuevo) -> existente
              ));
        }
      }

      if (topRoles != null && topRoles > 0) {
        Pageable limiteRol = PageRequest.of(0, topRoles.intValue());
        for (TipoAccionAuditoria accion : accionesInteres) {
          List<TopRolProjection> topRolesProyecciones = auditoriaRepository.findTopRolesPorAccion(
              desdeDateTime, hastaDateTime, accion, limiteRol);
          proyeccionesRolesPorAccion.put(accion, topRolesProyecciones);
        }
      }

      UsuariosReportesDTO usuariosReportesDTO = metricasMapper.mapearTopUsuariosYRoles(
          proyeccionesUsuariosPorAccion,
          mapaNombresUsuarios,
          proyeccionesRolesPorAccion,
          topUsuarios,
          topRoles
      );

      // Procesamiento Analítico e infraestructura del Mapa
      TotalBusquedasProjection totalProjection = registroBusquedaRepository.countBusquedasEnRango(
          desdeDateTime, hastaDateTime);
      long totalConsultas = (totalProjection != null && totalProjection.getTotal() != null)
          ? totalProjection.getTotal() : 0L;

      // Inicializamos en null para denotar que "no se calcularon los tops"
      List<TopEspacioBusquedaProjection> proyeccionesOrigenes = null;
      List<TopEspacioBusquedaProjection> proyeccionesDestinos = null;
      List<TopRutaBusquedaProjection> proyeccionesRutas = null;
      Map<Long, String> mapaNombresEspaciosMapa = Collections.emptyMap();

      if (topBusquedas != null && topBusquedas > 0) {
        Pageable limiteBusquedas = PageRequest.of(0, topBusquedas.intValue());

        proyeccionesOrigenes = registroBusquedaRepository.findTopOrigenes(desdeDateTime,
            hastaDateTime, limiteBusquedas);
        proyeccionesDestinos = registroBusquedaRepository.findTopDestinos(desdeDateTime,
            hastaDateTime, limiteBusquedas);
        proyeccionesRutas = registroBusquedaRepository.findTopRutas(desdeDateTime, hastaDateTime,
            limiteBusquedas);

        // Consolidar todos los IDs únicos
        Set<Long> todosLosEspacioIds = new HashSet<>();

        if (proyeccionesOrigenes != null) {
          proyeccionesOrigenes.stream().map(TopEspacioBusquedaProjection::getEspacioId)
              .forEach(todosLosEspacioIds::add);
        }
        if (proyeccionesDestinos != null) {
          proyeccionesDestinos.stream().map(TopEspacioBusquedaProjection::getEspacioId)
              .forEach(todosLosEspacioIds::add);
        }
        if (proyeccionesRutas != null) {
          proyeccionesRutas.stream().map(TopRutaBusquedaProjection::getDesdeId)
              .forEach(todosLosEspacioIds::add);
          proyeccionesRutas.stream().map(TopRutaBusquedaProjection::getHastaId)
              .forEach(todosLosEspacioIds::add);
        }

        if (!todosLosEspacioIds.isEmpty()) {
          List<EspacioNombreProjection> nombresMapaProyecciones = espacioRepository.findNombresByIds(
              todosLosEspacioIds);
          mapaNombresEspaciosMapa = nombresMapaProyecciones.stream()
              .collect(Collectors.toMap(EspacioNombreProjection::getId,
                  EspacioNombreProjection::getNombre, (a, b) -> a));
        }
      }

      BusquedasMapaDTO busquedasMapaDTO = metricasMapper.mapearBusquedasMapa(
          totalConsultas,
          proyeccionesOrigenes,
          proyeccionesDestinos,
          proyeccionesRutas,
          mapaNombresEspaciosMapa
      );

      // Devolver el DTO maestro extendido
      return new MetricaResponseDTO(
          desde,
          hasta,
          cuentasDTO,
          datosDTO,
          reportesDTO,
          usuariosReportesDTO,
          busquedasMapaDTO
      );
    } finally {
      procesandoReporte.set(false);
    }
  }
}
