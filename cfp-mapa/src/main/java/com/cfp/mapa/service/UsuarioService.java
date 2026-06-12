package com.cfp.mapa.service;

import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

  UsuarioResponseDTO crearUsuarioPorAdmin(UsuarioCreateRequestDTO request);

  Page<UsuarioResponseDTO> listarUsuariosConFiltro(
      String dni,
      String nombre,
      String apellido,
      Boolean activo,
      Pageable pageable
  );

  UsuarioResponseDTO restablecerPasswordPorAdmin(String dni);

  UsuarioResponseDTO cambiarEstadoActivoPorAdmin(String dni);

  void cambiarPassword(String dni, String oldPassword, String newPassword);

  UsuarioResponseDTO cambiarRolPorAdmin(String dni);

  UsuarioResponseDTO obtenerMiPerfil(String dni);
}
