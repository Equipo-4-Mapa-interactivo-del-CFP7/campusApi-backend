package com.cfp.mapa.repository;

import com.cfp.mapa.model.Conexion;
import com.cfp.mapa.model.enums.EstadoConexion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConexionRepository extends JpaRepository<Conexion, Long> {

    List<Conexion> findByEstadoOrderByIdAsc(EstadoConexion estado);

    List<Conexion> findByEstadoAndAccesibleTrueOrderByIdAsc(EstadoConexion estado);

}
