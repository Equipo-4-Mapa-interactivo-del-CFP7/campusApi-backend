package com.cfp.mapa.repository;

import com.cfp.mapa.model.AuditoriaUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaUsuario, Long> {

  @Query(value = "SELECT a FROM AuditoriaUsuario a " +
      "JOIN FETCH a.operador " +
      "JOIN FETCH a.usuarioAfectado",
      countQuery = "SELECT COUNT(a) FROM AuditoriaUsuario a")
  Page<AuditoriaUsuario> findAllCompleto(Pageable pageable);
}