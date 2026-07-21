package com.cfp.mapa.repository;

import com.cfp.mapa.model.RegistroBusqueda;
import com.cfp.mapa.repository.projection.TopEspacioBusquedaProjection;
import com.cfp.mapa.repository.projection.TopRutaBusquedaProjection;
import com.cfp.mapa.repository.projection.TotalBusquedasProjection;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegistroBusquedaRepository extends JpaRepository<RegistroBusqueda, Long> {

  // Conteo total de busquedas en el rango
  @Query("SELECT COUNT(r.id) AS total FROM RegistroBusqueda r WHERE r.fecha BETWEEN :desde AND :hasta")
  TotalBusquedasProjection countBusquedasEnRango(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta
  );

  // TOP Origenes (desde_id)
  @Query("SELECT r.desdeId AS espacioId, COUNT(r.id) AS cantidad " +
      "FROM RegistroBusqueda r " +
      "WHERE r.fecha BETWEEN :desde AND :hasta " +
      "AND r.desdeId IS NOT NULL " +
      "GROUP BY r.desdeId " +
      "ORDER BY COUNT(r.id) DESC")
  List<TopEspacioBusquedaProjection> findTopOrigenes(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta,
      Pageable pageable
  );

  // TOP Destinos (hasta_id)
  @Query("SELECT r.hastaId AS espacioId, COUNT(r.id) AS cantidad " +
      "FROM RegistroBusqueda r " +
      "WHERE r.fecha BETWEEN :desde AND :hasta " +
      "AND r.hastaId IS NOT NULL " +
      "GROUP BY r.hastaId " +
      "ORDER BY COUNT(r.id) DESC")
  List<TopEspacioBusquedaProjection> findTopDestinos(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta,
      Pageable pageable
  );

  // TOP Rutas Completas (desde_id -> hasta_id)
  @Query("SELECT r.desdeId AS desdeId, r.hastaId AS hastaId, COUNT(r.id) AS cantidad " +
      "FROM RegistroBusqueda r " +
      "WHERE r.fecha BETWEEN :desde AND :hasta " +
      "AND r.desdeId IS NOT NULL " +
      "AND r.hastaId IS NOT NULL " +
      "GROUP BY r.desdeId, r.hastaId " +
      "ORDER BY COUNT(r.id) DESC")
  List<TopRutaBusquedaProjection> findTopRutas(
      @Param("desde") LocalDateTime desde,
      @Param("hasta") LocalDateTime hasta,
      Pageable pageable
  );
}
