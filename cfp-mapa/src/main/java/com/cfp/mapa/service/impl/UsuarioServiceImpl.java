package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.usuario.UsuarioCreateRequestDTO;
import com.cfp.mapa.dto.usuario.UsuarioResponseDTO;
import com.cfp.mapa.exception.DniDuplicadoException;
import com.cfp.mapa.mapper.UsuarioMapper;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UsuarioServiceImpl {

  private final UsuarioRepository usuarioRepository;
  private final UsuarioMapper usuarioMapper;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UsuarioResponseDTO crearUsuarioPorAdmin(UsuarioCreateRequestDTO request) {

    if (usuarioRepository.existsByDni(request.dni())) {
      throw new DniDuplicadoException(request.dni());
    }

    String passwordGenerica = request.dni();
    String encodedPassword = passwordEncoder.encode(passwordGenerica);

    Usuario usuarioGuardado = usuarioMapper.createToUsuario(request, encodedPassword);
    usuarioRepository.save(usuarioGuardado);

    return usuarioMapper.usuarioToResponse(usuarioGuardado);
  }
}
