package com.cfp.mapa.repository;

import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @Query("SELECT r FROM Reporte r WHERE " +
        "(:espacioId IS NULL OR r.espacio.id = :espacioId) AND " +
        "(:estado IS NULL OR r.estado = :estado) AND " +
        "(:tipo IS NULL OR r.tipo = :tipo)")
    Page<Reporte> buscarReportesDinamico(
        @Param("espacioId") Long espacioId,
        @Param("estado") EstadoReporte estado,
        @Param("tipo") TipoReporte tipo,
        Pageable pageable
    );

    boolean existsByEspacioAndTipoAndEstadoIn(Espacio espacio, TipoReporte tipo, Collection<EstadoReporte> estados);

    List<Reporte> findByEstadoInAndFechaVencimientoLessThanEqual(
        Collection<EstadoReporte> estados,
        LocalDateTime fecha
    );

}
