package com.cfp.mapa.repository;

import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.Rol;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByDni(String dni);

  boolean existsByDni(String dni);

  @Query("SELECT u FROM Usuario u WHERE " +
      "(:dni IS NULL OR u.dni LIKE %:dni%) AND " +
      "(:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
      "(:apellido IS NULL OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :apellido, '%'))) AND " +
      "(:activo IS NULL OR u.activo = :activo)"
  )
  Page<Usuario> buscarUsuariosDinamico(
      @Param("dni") String dni,
      @Param("nombre") String nombre,
      @Param("apellido") String apellido,
      @Param("activo") Boolean activo,
      Pageable pageable
  );

  boolean existsByIdAndActivoTrueAndRolIn(Long id, Collection<Rol> roles);
}
