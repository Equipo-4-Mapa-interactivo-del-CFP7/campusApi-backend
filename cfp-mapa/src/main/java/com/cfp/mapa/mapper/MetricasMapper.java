package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.metricas.BusquedasMapaDTO;
import com.cfp.mapa.dto.metricas.CuentasDTO;
import com.cfp.mapa.dto.metricas.DatosDTO;
import com.cfp.mapa.dto.metricas.EdicionDTO;
import com.cfp.mapa.dto.metricas.EspacioBusquedaDTO;
import com.cfp.mapa.dto.metricas.EspacioDTO;
import com.cfp.mapa.dto.metricas.RendimientoDTO;
import com.cfp.mapa.dto.metricas.ReportesDTO;
import com.cfp.mapa.dto.metricas.RolDTO;
import com.cfp.mapa.dto.metricas.RutaBusquedaDTO;
import com.cfp.mapa.dto.metricas.TopEspaciosDTO;
import com.cfp.mapa.dto.metricas.TopRolesDTO;
import com.cfp.mapa.dto.metricas.TopUsuariosDTO;
import com.cfp.mapa.dto.metricas.UsuarioDTO;
import com.cfp.mapa.dto.metricas.UsuariosReportesDTO;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.model.enums.TipoReporte;
import com.cfp.mapa.repository.projection.CantidadPorAccionProjection;
import com.cfp.mapa.repository.projection.EdicionDatosProjection;
import com.cfp.mapa.repository.projection.RendimientoReporteProjection;
import com.cfp.mapa.repository.projection.TopEspacioBusquedaProjection;
import com.cfp.mapa.repository.projection.TopEspacioProjection;
import com.cfp.mapa.repository.projection.TopRolProjection;
import com.cfp.mapa.repository.projection.TopRutaBusquedaProjection;
import com.cfp.mapa.repository.projection.TopUsuarioProjection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MetricasMapper {

  public CuentasDTO mapearCuentas(List<CantidadPorAccionProjection> proyecciones){

    Map<TipoAccionAuditoria, Long> mapa = proyecciones.stream()
        .collect(Collectors.toMap(
            CantidadPorAccionProjection::getAccion,
            CantidadPorAccionProjection::getCantidad
        ));

    return new CuentasDTO(
        mapa.getOrDefault(TipoAccionAuditoria.USUARIO_CREADO ,0L),
        mapa.getOrDefault(TipoAccionAuditoria.USUARIO_ELIMINADO ,0L),
        mapa.getOrDefault(TipoAccionAuditoria.PASSWORD_RESTABLECIDA ,0L),
        mapa.getOrDefault(TipoAccionAuditoria.PASSWORD_CAMBIADA ,0L),
        mapa.getOrDefault(TipoAccionAuditoria.ESTADO_ACTIVO_MODIFICADO ,0L),
        mapa.getOrDefault(TipoAccionAuditoria.ROL_MODIFICADO ,0L),
        mapa.getOrDefault(TipoAccionAuditoria.PASSWORD_OWNER_RECUPERADA ,0L),
        mapa.getOrDefault(TipoAccionAuditoria.OWNER_TRANSFERIDO ,0L)
    );
  }

  public DatosDTO mapearDatos(List<EdicionDatosProjection> proyecciones){

    EdicionDTO dniDTO = new EdicionDTO(0L, 0L, 0L);
    EdicionDTO nombreDTO = new EdicionDTO(0L, 0L, 0L);

    for (EdicionDatosProjection p : proyecciones) {

      long siMismo = p.getConteoSiMismo() != null ? p.getConteoSiMismo() : 0L;
      long otros = p.getConteoOtros() != null ? p.getConteoOtros() : 0L;
      long total = siMismo + otros;

      if (p.getAccion() == TipoAccionAuditoria.DNI_EDITADO) {
        dniDTO = new EdicionDTO(total, siMismo, otros);
      } else if (p.getAccion() == TipoAccionAuditoria.NOMBRE_APELLIDO_EDITADO) {
        nombreDTO = new EdicionDTO(total, siMismo, otros);
      }
    }

    return new DatosDTO(dniDTO, nombreDTO);
  }

  public List<RendimientoDTO> mapearRendimiento(List<RendimientoReporteProjection> proyecciones){

    Map<TipoReporte, RendimientoReporteProjection> mapa = proyecciones.stream()
        .collect(Collectors.toMap(
            RendimientoReporteProjection::getTipo,
            p -> p
        ));

    return Arrays.stream(TipoReporte.values())
        .map(tipo -> {
          RendimientoReporteProjection p = mapa.get(tipo);
          long creados = (p != null && p.getCreados() != null) ? p.getCreados() : 0L;
          long cerrados = (p != null && p.getCerrados() != null) ? p.getCerrados() : 0L;
          long promedio = (p != null && p.getPromedioMinutos() != null) ? (long) Math.ceil(p.getPromedioMinutos()) : 0L;
          long maximoMinutos = (p != null && p.getMaxMinutos() != null) ? p.getMaxMinutos() : 0L;

          return new RendimientoDTO(tipo, creados, cerrados, promedio, maximoMinutos);
        })
        .toList();
  }

  public ReportesDTO mapearReportes(
      List<CantidadPorAccionProjection> proyecciones,
      List<RendimientoDTO> rendimiento,
      List<TopEspaciosDTO> topEspacios
  ) {

    Map<TipoAccionAuditoria, Long> mapa = proyecciones.stream()
        .collect(Collectors.toMap(
            CantidadPorAccionProjection::getAccion,
            CantidadPorAccionProjection::getCantidad,
            (a, b) -> a
        ));

    return new ReportesDTO(
        mapa.getOrDefault(TipoAccionAuditoria.REPORTE_CREADO, 0L),
        mapa.getOrDefault(TipoAccionAuditoria.REPORTE_ATENDIDO, 0L),
        mapa.getOrDefault(TipoAccionAuditoria.REPORTE_MODIFICADO, 0L) +
            mapa.getOrDefault(TipoAccionAuditoria.REPORTE_TIEMPO_ELIMINADO, 0L),
        mapa.getOrDefault(TipoAccionAuditoria.REPORTE_CERRADO, 0L),
        mapa.getOrDefault(TipoAccionAuditoria.REPORTE_CERRADO_AUTOMATICO, 0L),
        rendimiento,
        topEspacios
    );
  }

  public List<TopEspaciosDTO> mapearTopEspacios(
      List<TopEspacioProjection> proyecciones,
      Map<Long, String> mapaNombres,
      long topLimit
  ) {
    if (proyecciones.isEmpty()) {
      return Arrays.stream(TipoReporte.values())
          .map(tipo -> new TopEspaciosDTO(tipo, Collections.emptyList()))
          .toList();
    }

    // Agrupar las proyecciones por TipoReporte
    Map<TipoReporte, List<TopEspacioProjection>> proyeccionesPorTipo = proyecciones.stream()
        .collect(Collectors.groupingBy(TopEspacioProjection::getTipo));

    return Arrays.stream(TipoReporte.values())
        .map(tipo -> {
          List<TopEspacioProjection> listaDelTipo = proyeccionesPorTipo.getOrDefault(tipo, Collections.emptyList());

          List<EspacioDTO> zonasDTO = listaDelTipo.stream()
              .limit(topLimit)
              .map(p -> new EspacioDTO(
                  p.getEspacioId(),
                  mapaNombres.getOrDefault(p.getEspacioId(), "Espacio no encontrado"),
                  p.getCantidad()
              ))
              .toList();

          return new TopEspaciosDTO(tipo, zonasDTO);
        })
        .toList();
  }

  public UsuariosReportesDTO mapearTopUsuariosYRoles(
      Map<TipoAccionAuditoria, List<TopUsuarioProjection>> usuariosPorAccion,
      Map<Long, String> mapaNombresUsuarios,
      Map<TipoAccionAuditoria, List<TopRolProjection>> rolesPorAccion,
      Long topUsuarios,
      Long topRoles
  ) {
    List<TopUsuariosDTO> listTopUsuariosDTO = null;
    List<TopRolesDTO> listTopRolesDTO = null;

    // DTO de Usuarios
    if (topUsuarios != null && topUsuarios > 0) {
      listTopUsuariosDTO = new ArrayList<>();
      for (Map.Entry<TipoAccionAuditoria, List<TopUsuarioProjection>> entry : usuariosPorAccion.entrySet()) {
        List<UsuarioDTO> usuariosDTO = entry.getValue().stream()
            .map(p -> new UsuarioDTO(
                p.getUsuarioId(),
                mapaNombresUsuarios.getOrDefault(p.getUsuarioId(), "Usuario no encontrado"),
                p.getCantidad() != null ? p.getCantidad() : 0L
            ))
            .toList();

        listTopUsuariosDTO.add(new TopUsuariosDTO(entry.getKey(), usuariosDTO));
      }
    }

    // DTO de Roles
    if (topRoles != null && topRoles > 0) {
      listTopRolesDTO = new ArrayList<>();
      for (Map.Entry<TipoAccionAuditoria, List<TopRolProjection>> entry : rolesPorAccion.entrySet()) {

        List<RolDTO> rolesDTO = entry.getValue().stream()
            .map(p -> {
              try {
                Rol rolEnum = Rol.valueOf(p.getRol());
                long cantidad = p.getCantidad() != null ? p.getCantidad() : 0L;
                return new RolDTO(rolEnum, cantidad);
              } catch (Exception e) {
                return null; // Si falla el valueOf, se descarta
              }
            })
            .filter(Objects::nonNull)
            .toList();

        listTopRolesDTO.add(new TopRolesDTO(entry.getKey(), rolesDTO));
      }
    }

    return new UsuariosReportesDTO(listTopUsuariosDTO, listTopRolesDTO);
  }

  public BusquedasMapaDTO mapearBusquedasMapa(
      long totalConsultas,
      List<TopEspacioBusquedaProjection> origenes,
      List<TopEspacioBusquedaProjection> destinos,
      List<TopRutaBusquedaProjection> rutas,
      Map<Long, String> mapaNombres
  ) {

    List<EspacioBusquedaDTO> topOrigenesDTO = (origenes != null)
        ? origenes.stream()
        .map(p -> new EspacioBusquedaDTO(
            p.getEspacioId(),
            mapaNombres.getOrDefault(p.getEspacioId(), "Espacio no encontrado"),
            p.getCantidad()
        ))
        .toList()
        : null;

    List<EspacioBusquedaDTO> topDestinosDTO = (destinos != null)
        ? destinos.stream()
        .map(p -> new EspacioBusquedaDTO(
            p.getEspacioId(),
            mapaNombres.getOrDefault(p.getEspacioId(), "Espacio no encontrado"),
            p.getCantidad()
        ))
        .toList()
        : null;

    List<RutaBusquedaDTO> topRutasDTO = (rutas != null)
        ? rutas.stream()
        .map(p -> new RutaBusquedaDTO(
            p.getDesdeId(),
            mapaNombres.getOrDefault(p.getDesdeId(), "Espacio no encontrado"),
            p.getHastaId(),
            mapaNombres.getOrDefault(p.getHastaId(), "Espacio no encontrado"),
            p.getCantidad()
        ))
        .toList()
        : null;

    return new BusquedasMapaDTO(
        totalConsultas,
        topOrigenesDTO,
        topDestinosDTO,
        topRutasDTO
    );
  }
}
