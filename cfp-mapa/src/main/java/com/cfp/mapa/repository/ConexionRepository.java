package com.cfp.mapa.repository;

import com.cfp.mapa.model.Conexion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConexionRepository extends JpaRepository<Conexion, Long> {

    List<Conexion> findByActivaTrue();

}
