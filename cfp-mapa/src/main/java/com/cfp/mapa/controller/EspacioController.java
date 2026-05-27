package com.cfp.mapa.controller;

import com.cfp.mapa.model.Espacio;
import com.cfp.mapa.model.enums.TipoEspacio;
import com.cfp.mapa.service.EspacioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/espacios")
@CrossOrigin(origins = "*")
public class EspacioController {

    private final EspacioService espacioService;

    public EspacioController(EspacioService espacioService) {
        this.espacioService = espacioService;
    }

    @GetMapping
    public ResponseEntity<List<Espacio>> listarTodos() {
        return ResponseEntity.ok(espacioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Espacio> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(espacioService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Espacio>> buscar(@RequestParam String nombre) {
        return ResponseEntity.ok(espacioService.buscarPorNombre(nombre));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Espacio>> listarPorTipo(@PathVariable TipoEspacio tipo) {
        return ResponseEntity.ok(espacioService.listarPorTipo(tipo));
    }

    @GetMapping("/accesibles")
    public ResponseEntity<List<Espacio>> listarAccesibles() {
        return ResponseEntity.ok(espacioService.listarAccesibles());
    }

    @PostMapping
    public ResponseEntity<Espacio> crear(@RequestBody Espacio espacio) {
        return ResponseEntity.ok(espacioService.crear(espacio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Espacio> actualizar(@PathVariable Long id, @RequestBody Espacio espacio) {
        return ResponseEntity.ok(espacioService.actualizar(id, espacio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        espacioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
