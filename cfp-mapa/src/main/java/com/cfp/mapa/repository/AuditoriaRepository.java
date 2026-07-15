package com.cfp.mapa.repository;

import com.cfp.mapa.model.AuditoriaUsuario;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
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
}