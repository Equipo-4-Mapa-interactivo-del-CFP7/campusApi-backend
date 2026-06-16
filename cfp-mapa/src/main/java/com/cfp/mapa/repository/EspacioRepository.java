package com.cfp.mapa.repository;

import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.enums.TipoEspacio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Long> {

    @Query("SELECT e FROM Espacio e WHERE " +
            "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:descripcion IS NULL OR LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))) AND " +
            "(:tipo IS NULL OR e.tipo = :tipo) AND " +
            "(:accesible IS NULL OR e.accesible = :accesible) AND " +
            "(:activo IS NULL OR e.activo = :activo)")

    Page<Espacio> buscarEspacios(
            @Param("nombre") String nombre,
            @Param("descripcion") String descripcion,
            @Param("tipo") TipoEspacio tipo,
            @Param("accesible") Boolean accesible,
            @Param("activo") Boolean activo,
            Pageable pageable
    );

    List<Espacio> findByActivoTrue();

    List<Espacio> findByActivoTrueAndTipo(TipoEspacio tipo);

    List<Espacio> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

}
