package com.cfp.mapa.service.impl;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.mapper.AuditoriaMapper;
import com.cfp.mapa.model.AuditoriaUsuario;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.repository.AuditoriaRepository;
import com.cfp.mapa.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

  private final AuditoriaRepository auditoriaRepository;
  private final AuditoriaMapper auditoriaMapper;

  @Transactional(readOnly = true)
  @Override
  public Page<AuditoriaResponseDTO> listarHistorialPaginado(int pagina, int tamaño, String direccion) {
    Sort orden = direccion.equalsIgnoreCase("asc")
        ? Sort.by("fechaAccion").ascending()
        : Sort.by("fechaAccion").descending();

    Pageable pageable = PageRequest.of(pagina, tamaño, orden);
    Page<AuditoriaUsuario> auditorias = auditoriaRepository.findAllCompleto(pageable);

    return auditorias.map(auditoriaMapper::toDTO);
  }

  @Transactional
  @Override
  public void registrarAccion(Usuario operador, Usuario afectado, TipoAccionAuditoria accion) {
    AuditoriaUsuario nuevaAuditoria = new AuditoriaUsuario(operador, afectado, accion.getDescripcion());
    auditoriaRepository.save(nuevaAuditoria);
  }
}