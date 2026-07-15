package com.cfp.mapa.controller;

import com.cfp.mapa.dto.recorrido.RutaRequestDTO;
import com.cfp.mapa.dto.recorrido.RutaResponseDTO;
import com.cfp.mapa.service.RecorridoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recorridos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500/")
public class RecorridoController {

    private final RecorridoService recorridoService;

    @PostMapping("/calcular")
    public ResponseEntity<RutaResponseDTO> calcularRuta(@RequestBody RutaRequestDTO dto){

        RutaResponseDTO ruta =
                recorridoService.calcularRuta(
                        dto.origenId(),
                        dto.destinoId(),
                        dto.soloAccesible()
                );
        return ResponseEntity.ok(ruta);
    }
}