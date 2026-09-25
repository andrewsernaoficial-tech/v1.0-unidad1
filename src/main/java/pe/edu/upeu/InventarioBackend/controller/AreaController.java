package pe.edu.upeu.InventarioBackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.entity.Area;
import pe.edu.upeu.InventarioBackend.service.service.AreaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @GetMapping
    public ResponseEntity<List<Area>> listar() {
        return ResponseEntity.ok(areaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Area> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Area> crear(@Valid @RequestBody Area area) {
        return ResponseEntity.status(HttpStatus.CREATED).body(areaService.crear(area));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Area> actualizar(@PathVariable Long id, @Valid @RequestBody Area area) {
        return ResponseEntity.ok(areaService.actualizar(id, area));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        areaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}