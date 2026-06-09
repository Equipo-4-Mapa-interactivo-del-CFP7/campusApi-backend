package com.cfp.mapa.controller;

import com.cfp.mapa.model.enums.EstadoReporte;
import com.cfp.mapa.model.Reporte;
import com.cfp.mapa.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

  // TODO: crear reporte especial

//    private final ReporteService reporteService;
//
//    public ReporteController(ReporteService reporteService) {
//        this.reporteService = reporteService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Reporte>> listarTodos() {
//        return ResponseEntity.ok(reporteService.listarTodos());
//    }
//
//    @GetMapping("/espacio/{espacioId}")
//    public ResponseEntity<List<Reporte>> listarPorEspacio(@PathVariable Long espacioId) {
//        return ResponseEntity.ok(reporteService.listarPorEspacio(espacioId));
//    }
//
//    @GetMapping("/estado/{estado}")
//    public ResponseEntity<List<Reporte>> listarPorEstado(@PathVariable EstadoReporte estado) {
//        return ResponseEntity.ok(reporteService.listarPorEstado(estado));
//    }
//
//    @PostMapping
//    public ResponseEntity<Reporte> crear(@RequestBody Reporte reporte) {
//        return ResponseEntity.ok(reporteService.crear(reporte));
//    }
//
//    @PatchMapping("/{id}/estado")
//    public ResponseEntity<Reporte> actualizarEstado(
//            @PathVariable Long id,
//            @RequestParam EstadoReporte nuevoEstado) {
//        return ResponseEntity.ok(reporteService.actualizarEstado(id, nuevoEstado));
//    }
}
