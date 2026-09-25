package pe.edu.upeu.InventarioBackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.dto.ProductoDespachadoDTO;
import pe.edu.upeu.InventarioBackend.service.service.DespachoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final DespachoService despachoService;

    @GetMapping("/productos-despachados")
    public ResponseEntity<List<ProductoDespachadoDTO>> obtenerReporte(
            @RequestParam String periodo,
            @RequestParam(required = false) Long categoriaId) {
        return ResponseEntity.ok(despachoService.obtenerReporteProductosDespachados(periodo, categoriaId));
    }
}