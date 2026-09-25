package pe.edu.upeu.InventarioBackend.service.service;

import pe.edu.upeu.InventarioBackend.dto.DespachoRequestDTO;
import pe.edu.upeu.InventarioBackend.dto.ProductoDespachadoDTO;
import pe.edu.upeu.InventarioBackend.entity.Despacho;

import java.util.List;

public interface DespachoService {
    Despacho registrar(DespachoRequestDTO dto);
    List<Despacho> listar();
    Despacho buscarPorId(Long id);
    void anular(Long id);
    List<ProductoDespachadoDTO> obtenerReporteProductosDespachados(String periodo, Long categoriaId);
}