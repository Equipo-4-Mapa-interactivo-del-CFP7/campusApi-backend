package com.cfp.mapa.repository;

import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByEspacioId(Long espacioId);
    List<Reporte> findByUsuarioId(Long usuarioId);
    List<Reporte> findByEstado(EstadoReporte estado);
}
