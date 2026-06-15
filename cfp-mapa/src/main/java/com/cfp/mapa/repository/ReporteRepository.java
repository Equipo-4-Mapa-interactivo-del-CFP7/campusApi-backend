package com.cfp.mapa.repository;

import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.enums.TipoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ReporteRepository
        extends JpaRepository<Reporte, Long> {

    List<Reporte> findByEstado(
            EstadoReporte estado
    );

    List<Reporte> findByTipoReporte(
            TipoReporte tipoReporte
    );

    Page<Reporte> buscarReportesDinamico(
            @Param("id") Long id,
            @Param("estado") EstadoReporte estado,
            @Param("tipo") TipoReporte tipo,
            Pageable pageable
    );

}
