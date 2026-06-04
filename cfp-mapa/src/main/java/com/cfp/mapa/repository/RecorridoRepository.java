package com.cfp.mapa.repository;

import com.cfp.mapa.model.Recorrido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecorridoRepository extends JpaRepository<Recorrido, Long> {
//    List<Recorrido> findByAccesibleTrue();
}
