package com.cfp.mapa.repository;

import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.enums.EstadoEspacio;
import com.cfp.mapa.model.enums.TipoEspacio;
import com.cfp.mapa.repository.projection.EspacioNombreProjection;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Long> {

    @Query(""" 
        SELECT e FROM Espacio e
        WHERE (:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        AND (:descripcion IS NULL OR LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')))
        AND (:tipo IS NULL OR e.tipo = :tipo)
        AND (:accesible IS NULL OR e.accesible = :accesible)
        AND (:estado IS NULL OR e.estado = :estado)
        """)
    Page<Espacio> buscarEspacios(
            String nombre,
            String descripcion,
            TipoEspacio tipo,
            Boolean accesible,
            EstadoEspacio estado,
            Pageable pageable
    );

    List<Espacio> findByEstado(EstadoEspacio estado);

    List<Espacio> findByEstadoAndTipo(EstadoEspacio estado, TipoEspacio tipo);

    List<Espacio> findByNombreContainingIgnoreCaseAndEstado(String nombre, EstadoEspacio estado);

    @Query("SELECT e.id AS id, e.nombre AS nombre FROM Espacio e WHERE e.id IN :ids")
    List<EspacioNombreProjection> findNombresByIds(@Param("ids") Collection<Long> ids);
}
