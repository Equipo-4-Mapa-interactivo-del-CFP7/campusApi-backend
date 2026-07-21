package com.cfp.mapa.repository;

import com.cfp.mapa.model.AuditoriaUsuario;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.repository.projection.CantidadPorAccionProjection;
import com.cfp.mapa.repository.projection.EdicionDatosProjection;
import com.cfp.mapa.repository.projection.RendimientoReporteProjection;
import com.cfp.mapa.repository.projection.TopEspacioProjection;
import com.cfp.mapa.repository.projection.TopRolProjection;
import com.cfp.mapa.repository.projection.TopUsuarioProjection;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditoriaRepository extends JpaRepository<AuditoriaUsuario, Long> {

  Page<AuditoriaUsuario> findByOperadorIdOrUsuarioAfectadoId(Long operadorId, Long usuarioAfectadoId, Pageable pageable);

  @Query("SELECT a FROM AuditoriaUsuario a WHERE " +
      "(:usuarioId IS NULL OR a.operadorId = :usuarioId OR a.usuarioAfectadoId = :usuarioId) AND " +
      "(:accion IS NULL OR a.accion IN :accion) AND " +
      "(:reporteId IS NULL OR a.reporteId = :reporteId)")
  Page<AuditoriaUsuario> buscarConFiltrosDinamicos(
      @Param("usuarioId") Long usuarioId,
      @Param("accion") List<TipoAccionAuditoria> accion,
      @Param("reporteId") Long reporteId,
      Pageable pageable
  );

  @Query("SELECT a FROM AuditoriaUsuario a " +
      "WHERE a.fechaAccion BETWEEN :desde AND :hasta")
  List<AuditoriaUsuario> buscarPorRangoFechas(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta
  );

  // ======================================
  // METRICAS ENTRE FECHAS
  // ======================================

  // Conteo global para Cuentas y Reportes (Agrupados por acción en un solo viaje)
  @Query("SELECT a.accion AS accion, COUNT(a.id) AS cantidad " +
      "FROM AuditoriaUsuario a " +
      "WHERE a.fechaAccion BETWEEN :desde AND :hasta " +
      "GROUP BY a.accion")
  List<CantidadPorAccionProjection> countAccionesEnRango(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta
  );

  // Conteo de cambios de datos (DNI / Nombre y Apellido) evaluando si usuarioAfectadoId es nulo o no
  @Query("SELECT a.accion AS accion, " +
      "SUM(CASE WHEN a.usuarioAfectadoId IS NULL THEN 1 ELSE 0 END) AS conteoSiMismo, " +
      "SUM(CASE WHEN a.usuarioAfectadoId IS NOT NULL THEN 1 ELSE 0 END) AS conteoOtros " +
      "FROM AuditoriaUsuario a " +
      "WHERE a.fechaAccion BETWEEN :desde AND :hasta " +
      "AND a.accion IN :accionesEdicion " +
      "GROUP BY a.accion")
  List<EdicionDatosProjection> countEdicionesEnRango(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta,
      @Param("accionesEdicion") List<TipoAccionAuditoria> accionesEdicion
  );

  // LA CONSULTA MAESTRA: Rendimiento por Tipo de Reporte (Usa el índice compuesto para el JOIN interno)
  @Query("SELECT c.reporteTipo AS tipo, " +
      "COUNT(CASE WHEN c.accion = :accionCreado THEN 1 END) AS creados, " +
      "COUNT(CASE WHEN c.accion IN :accionesCerrado THEN 1 END) AS cerrados, " +
      "AVG(CAST(CASE WHEN c.accion IN :accionesCerrado AND o.fechaAccion IS NOT NULL " +
      "THEN TIMESTAMPDIFF(MINUTE, o.fechaAccion, c.fechaAccion) END AS double)) AS promedioMinutos, " +
      "MAX(CAST(CASE WHEN c.accion IN :accionesCerrado AND o.fechaAccion IS NOT NULL " +
      "THEN TIMESTAMPDIFF(MINUTE, o.fechaAccion, c.fechaAccion) END AS long)) AS maxMinutos " +
      "FROM AuditoriaUsuario c " +
      "LEFT JOIN AuditoriaUsuario o ON c.reporteId = o.reporteId " +
      "AND o.accion = :accionCreado " +
      "WHERE c.fechaAccion BETWEEN :desde AND :hasta " +
      "GROUP BY c.reporteTipo")
  List<RendimientoReporteProjection> findRendimientoPorTipo(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta,
      @Param("accionCreado") TipoAccionAuditoria accionCreado,
      @Param("accionesCerrado") List<TipoAccionAuditoria> accionesCerrado
  );

  // TOP ESPACIOS: Agrupa por tipo de reporte y espacio, contando las creaciones
  @Query("SELECT a.reporteTipo AS tipo, a.reporteEspacioId AS espacioId, COUNT(a.id) AS cantidad " +
      "FROM AuditoriaUsuario a " +
      "WHERE a.fechaAccion BETWEEN :desde AND :hasta " +
      "AND a.accion = :accionCreado " +
      "AND a.reporteEspacioId IS NOT NULL " +
      "GROUP BY a.reporteTipo, a.reporteEspacioId " +
      "ORDER BY a.reporteTipo ASC, COUNT(a.id) DESC")
  List<TopEspacioProjection> findTopEspaciosEnRango(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta,
      @Param("accionCreado") TipoAccionAuditoria accionCreado
  );

  // TOP USUARIOS: Obtiene los usuarios más activos para una acción específica (CREADO, ATENDIDO, CERRADO)
  @Query("SELECT a.operadorId AS usuarioId, COUNT(a.id) AS cantidad " +
      "FROM AuditoriaUsuario a " +
      "WHERE a.fechaAccion BETWEEN :desde AND :hasta " +
      "AND a.accion = :accion " +
      "GROUP BY a.operadorId " +
      "ORDER BY COUNT(a.id) DESC")
  List<TopUsuarioProjection> findTopUsuariosPorAccion(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta,
      @Param("accion") TipoAccionAuditoria accion,
      Pageable pageable
  );

  // TOP ROLES: Obtiene los roles más activos para una acción específica (CREADO, ATENDIDO, CERRADO)
  @Query("SELECT a.operadorRol AS rol, COUNT(a.id) AS cantidad " +
      "FROM AuditoriaUsuario a " +
      "WHERE a.fechaAccion BETWEEN :desde AND :hasta " +
      "AND a.accion = :accion " +
      "GROUP BY a.operadorRol " +
      "ORDER BY COUNT(a.id) DESC")
  List<TopRolProjection> findTopRolesPorAccion(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta,
      @Param("accion") TipoAccionAuditoria accion,
      Pageable pageable
  );
}