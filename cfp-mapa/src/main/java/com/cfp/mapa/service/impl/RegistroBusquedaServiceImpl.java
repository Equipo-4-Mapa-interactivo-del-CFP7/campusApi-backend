package com.cfp.mapa.service.impl;

import com.cfp.mapa.model.RegistroBusqueda;
import com.cfp.mapa.repository.RegistroBusquedaRepository;
import com.cfp.mapa.service.RegistroBusquedaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistroBusquedaServiceImpl implements RegistroBusquedaService {

  private final RegistroBusquedaRepository registroBusquedaRepository;

  @Async
  @Override
  public void registrarBusqueda(String desdeIdStr, String hastaIdStr) {

    try {

      Long desdeId = Long.parseLong(desdeIdStr);
      Long hastaId = Long.parseLong(hastaIdStr);

      if (desdeId.equals(hastaId)) {
        return;
      }

      RegistroBusqueda registro = new RegistroBusqueda(desdeId, hastaId);

      registroBusquedaRepository.save(registro);
    } catch (Exception _) {

    }
  }
}
