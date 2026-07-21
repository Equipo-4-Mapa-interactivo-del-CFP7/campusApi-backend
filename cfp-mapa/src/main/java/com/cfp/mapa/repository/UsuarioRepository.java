package com.cfp.mapa.repository;

import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.Rol;
import com.cfp.mapa.repository.projection.UsuarioNombreProjection;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByDni(String dni);

  boolean existsByDni(String dni);

  @Query("SELECT u FROM Usuario u WHERE " +
      "u.eliminado = false AND " +
      "u.dni != 'SYSTEM01' AND " +
      "(:dni IS NULL OR u.dni LIKE :dni) AND " +
      "(:nombre IS NULL OR u.nombre LIKE :nombre) AND " +
      "(:apellido IS NULL OR u.apellido LIKE :apellido) AND " +
      "(:activo IS NULL OR u.activo = :activo) AND " +
      "(:rol IS NULL OR u.rol = :rol)"
  )
  Page<Usuario> buscarUsuariosDinamico(
      @Param("dni") String dni,
      @Param("nombre") String nombre,
      @Param("apellido") String apellido,
      @Param("activo") Boolean activo,
      @Param("rol") Rol rol,
      Pageable pageable
  );

  boolean existsByIdAndActivoTrueAndEliminadoFalseAndRolIn(Long id, Collection<Rol> roles);

  @Query("SELECT u.id AS id, CONCAT(COALESCE(u.nombre, ''), ' ', COALESCE(u.apellido, '')) AS nombre FROM Usuario u WHERE u.id IN :ids")
  List<UsuarioNombreProjection> findNombresByIds(@Param("ids") Collection<Long> ids);
}
