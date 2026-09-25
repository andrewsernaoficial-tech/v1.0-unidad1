package pe.edu.upeu.InventarioBackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.InventarioBackend.dto.DespachoRequestDTO;
import pe.edu.upeu.InventarioBackend.dto.DespachoResponseDTO;
import pe.edu.upeu.InventarioBackend.entity.Despacho;
import pe.edu.upeu.InventarioBackend.service.service.DespachoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService despachoService;

    @PostMapping
    public ResponseEntity<DespachoResponseDTO> registrar(@Valid @RequestBody DespachoRequestDTO dto) {
        Despacho despacho = despachoService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(despacho));
    }

    @GetMapping
    public ResponseEntity<List<DespachoResponseDTO>> listar() {
        List<DespachoResponseDTO> response = despachoService.listar().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespachoResponseDTO> buscarPorId(@PathVariable Long id) {
        Despacho despacho = despachoService.buscarPorId(id);
        return ResponseEntity.ok(toResponseDTO(despacho));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<DespachoResponseDTO> anular(@PathVariable Long id) {
        despachoService.anular(id);
        Despacho despachoAnulado = despachoService.buscarPorId(id);
        return ResponseEntity.ok(toResponseDTO(despachoAnulado));
    }

    private DespachoResponseDTO toResponseDTO(Despacho despacho) {
        DespachoResponseDTO dto = new DespachoResponseDTO();
        dto.setId(despacho.getId());
        dto.setFecha(despacho.getFecha());
        if (despacho.getArea() != null) {
            dto.setAreaId(despacho.getArea().getId());
            dto.setAreaNombre(despacho.getArea().getNombre());
        }
        dto.setObservacion(despacho.getObservacion());
        dto.setTotalUnidades(despacho.getTotalUnidades());
        dto.setMontoTotal(despacho.getMontoTotal());
        dto.setEstado(despacho.getEstado());

        if (despacho.getDetalles() != null) {
            List<DespachoResponseDTO.DetalleResponse> detalles = despacho.getDetalles().stream().map(d -> {
                DespachoResponseDTO.DetalleResponse det = new DespachoResponseDTO.DetalleResponse();
                det.setId(d.getId());
                if (d.getProducto() != null) {
                    det.setProductoId(d.getProducto().getId());
                    det.setProductoCodigo(d.getProducto().getCodigo());
                    det.setProductoNombre(d.getProducto().getNombre());
                }
                det.setCantidad(d.getCantidad());
                det.setCostoUnitario(d.getCostoUnitario());
                det.setImporte(d.getImporte());
                return det;
            }).toList();
            dto.setDetalles(detalles);
        }

        return dto;
    }
}