package pe.edu.upeu.InventarioBackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.dto.ProductoRequestDTO;
import pe.edu.upeu.InventarioBackend.entity.Categoria;
import pe.edu.upeu.InventarioBackend.entity.Producto;
import pe.edu.upeu.InventarioBackend.service.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Boolean stockBajo,
            @RequestParam(required = false, defaultValue = "nombre") String orden,
            @RequestParam(required = false, defaultValue = "asc") String dir) {
        return ResponseEntity.ok(productoService.buscar(nombre, categoriaId, stockBajo, orden, dir));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        Producto p = toEntity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        Producto p = toEntity(dto);
        return ResponseEntity.ok(productoService.actualizar(id, p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Producto toEntity(ProductoRequestDTO dto) {
        Producto p = new Producto();
        p.setCodigo(dto.getCodigo());
        p.setNombre(dto.getNombre());
        p.setCostoUnitario(dto.getCostoUnitario());
        p.setStock(dto.getStock());
        p.setStockMinimo(dto.getStockMinimo());
        p.setEstado(dto.getEstado());

        Categoria c = new Categoria();
        c.setId(dto.getCategoriaId());
        p.setCategoria(c);
        return p;
    }
}