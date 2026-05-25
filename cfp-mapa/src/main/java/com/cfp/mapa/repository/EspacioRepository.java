package com.cfp.mapa.repository;

import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.TipoEspacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Long> {
    List<Espacio> findByActivoTrue();
    List<Espacio> findByTipoAndActivoTrue(TipoEspacio tipo);
    List<Espacio> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    List<Espacio> findByAccesibleTrueAndActivoTrue();
}
