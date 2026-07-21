package com.cfp.mapa.controller;

import com.cfp.mapa.dto.registro.RegistrarBusquedaDTO;
import com.cfp.mapa.service.RegistroBusquedaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/busqueda")
public class RegistroBusquedaController {

  private final RegistroBusquedaService registroBusquedaService;

  @PostMapping("/registrar")
  public ResponseEntity<Void> registrarBusqueda(
      @RequestBody RegistrarBusquedaDTO request
  ) {

    registroBusquedaService.registrarBusqueda(request.desdeId(), request.hastaId());

    return ResponseEntity
        .status(HttpStatus.ACCEPTED)
        .build();
  }
}
