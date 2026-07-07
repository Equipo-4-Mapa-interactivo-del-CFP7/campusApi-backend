package com.cfp.mapa.repository;

import com.cfp.mapa.model.AuditoriaUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditoriaRepository extends JpaRepository<AuditoriaUsuario, Long> {

  Page<AuditoriaUsuario> findByOperadorIdOrUsuarioAfectadoId(Long operadorId, Long usuarioAfectadoId, Pageable pageable);

  @Query("SELECT a FROM AuditoriaUsuario a WHERE " +
      "(:usuarioId IS NULL OR a.operadorId = :usuarioId OR a.usuarioAfectadoId = :usuarioId) AND " +
      "(:accion IS NULL OR a.accion = :accion) AND " +
      "(:reporteId IS NULL OR a.reporteId = :reporteId)")
  Page<AuditoriaUsuario> buscarConFiltrosDinamicos(
      @Param("usuarioId") Long usuarioId,
      @Param("accion") String accion,
      @Param("reporteId") Long reporteId,
      Pageable pageable
  );
}