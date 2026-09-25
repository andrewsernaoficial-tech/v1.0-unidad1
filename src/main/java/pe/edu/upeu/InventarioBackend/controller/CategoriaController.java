package pe.edu.upeu.InventarioBackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.dto.CategoriaRequestDTO;
import pe.edu.upeu.InventarioBackend.dto.CategoriaResponseDTO;
import pe.edu.upeu.InventarioBackend.entity.Categoria;
import pe.edu.upeu.InventarioBackend.entity.Producto;
import pe.edu.upeu.InventarioBackend.service.service.CategoriaService;
import pe.edu.upeu.InventarioBackend.service.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listar() {
        List<CategoriaResponseDTO> list = categoriaService.listar().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(categoriaService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO dto) {
        Categoria c = new Categoria();
        c.setNombre(dto.getNombre());
        c.setDescripcion(dto.getDescripcion());
        c.setEstado(dto.getEstado());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(categoriaService.crear(c)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) {
        Categoria c = new Categoria();
        c.setNombre(dto.getNombre());
        c.setDescripcion(dto.getDescripcion());
        c.setEstado(dto.getEstado());
        return ResponseEntity.ok(toResponseDTO(categoriaService.actualizar(id, c)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<Producto>> listarProductosPorCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.listarPorCategoria(id));
    }

    private CategoriaResponseDTO toResponseDTO(Categoria c) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setDescripcion(c.getDescripcion());
        dto.setEstado(c.getEstado());
        dto.setFechaCreacion(c.getFechaCreacion());
        return dto;
    }
}