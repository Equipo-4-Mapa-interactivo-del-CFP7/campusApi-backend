package com.cfp.mapa.repository;

import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByEstado(EstadoReporte estado);

    List<Reporte> findByTipo(TipoReporte tipo);

    @Query("SELECT r FROM Reporte r WHERE " +
        "(:id IS NULL OR r.id = :id) AND " +
        "(:estado IS NULL OR r.estado = :estado) AND " +
        "(:tipo IS NULL OR r.tipo = :tipo)")
    Page<Reporte> buscarReportesDinamico(
        @Param("id") Long id,
        @Param("estado") EstadoReporte estado,
        @Param("tipo") TipoReporte tipo,
        Pageable pageable
    );

}
