package pe.edu.upeu.InventarioBackend.service.service;

import pe.edu.upeu.InventarioBackend.entity.Producto;
import pe.edu.upeu.InventarioBackend.service.generic.CrudService;

import java.util.List;

public interface ProductoService extends CrudService<Producto, Long> {
    List<Producto> listarPorCategoria(Long categoriaId);
    List<Producto> buscar(String nombre, Long categoriaId, Boolean stockBajo, String orden, String dir);
}