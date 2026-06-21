package com.cfp.mapa.service;

import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

  UsuarioResponseDTO crearUsuario(UsuarioCreateRequestDTO request);

  Page<UsuarioResponseDTO> listarUsuariosConFiltro(
      String dni,
      String nombre,
      String apellido,
      Boolean activo,
      String rol,
      Pageable pageable
  );

  UsuarioResponseDTO restablecerPassword(Long id);

  UsuarioResponseDTO cambiarEstadoActivo(Long id);

  void cambiarPassword(Long id, String oldPassword, String newPassword);

  UsuarioResponseDTO cambiarRol(Long id, String rol);

  UsuarioResponseDTO obtenerMiPerfil(Long id);

  UsuarioResponseDTO obtenerPerfil(Long id);

  void eliminarUsuario(Long id);

  void recuperarPasswordOwner(String dni, String recoveryPassword, String nuevaPassword);

  void transferirOwner(String password, Long id);
}
