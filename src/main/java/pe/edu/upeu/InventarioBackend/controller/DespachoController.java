package pe.edu.upeu.InventarioBackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.dto.DespachoRequestDTO;
import pe.edu.upeu.InventarioBackend.entity.Despacho;
import pe.edu.upeu.InventarioBackend.service.impl.DespachoServiceImpl;

@RestController
@RequestMapping("/api/v1/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoServiceImpl despachoService;

    @PostMapping
    public ResponseEntity<Despacho> registrar(@Valid @RequestBody DespachoRequestDTO dto) {
        Despacho creado = despachoService.registrarDespacho(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<Void> anular(@PathVariable Long id) {
        despachoService.anularDespacho(id);
        return ResponseEntity.ok().build();
    }
}