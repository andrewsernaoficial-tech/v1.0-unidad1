package pe.edu.upeu.InventarioBackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.InventarioBackend.entity.Categoria;
import pe.edu.upeu.InventarioBackend.entity.Producto;
import pe.edu.upeu.InventarioBackend.exeption.RecursoNoEncontradoException;
import pe.edu.upeu.InventarioBackend.exeption.ReglaNegocioException;
import pe.edu.upeu.InventarioBackend.repository.CategoriaRepository;
import pe.edu.upeu.InventarioBackend.repository.DespachoRepository;
import pe.edu.upeu.InventarioBackend.repository.ProductoRepository;
import pe.edu.upeu.InventarioBackend.service.service.ProductoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DespachoRepository despachoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(Long categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new RecursoNoEncontradoException("Categoría no encontrada con ID: " + categoriaId);
        }
        return productoRepository.findByCategoriaId(categoriaId);
    }

    @Override
    @Transactional
    public Producto crear(Producto producto) {
        if (productoRepository.existsByCodigoIgnoreCase(producto.getCodigo())) {
            throw new ReglaNegocioException("Ya existe un producto registrado con el código: " + producto.getCodigo());
        }

        Categoria categoria = categoriaRepository.findById(producto.getCategoria().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("La categoría asociada no existe"));

        producto.setCategoria(categoria);
        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public Producto actualizar(Long id, Producto productoActualizado) {
        Producto existente = buscarPorId(id);

        if (productoRepository.existsByCodigoIgnoreCaseAndIdNot(productoActualizado.getCodigo(), id)) {
            throw new ReglaNegocioException("Ya existe otro producto con el código: " + productoActualizado.getCodigo());
        }

        if (productoActualizado.getCategoria() != null && productoActualizado.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(productoActualizado.getCategoria().getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("La categoría asociada no existe"));
            existente.setCategoria(categoria);
        }

        existente.setCodigo(productoActualizado.getCodigo());
        existente.setNombre(productoActualizado.getNombre());
        existente.setCostoUnitario(productoActualizado.getCostoUnitario());
        existente.setStock(productoActualizado.getStock());
        existente.setStockMinimo(productoActualizado.getStockMinimo());
        if (productoActualizado.getEstado() != null) {
            existente.setEstado(productoActualizado.getEstado());
        }

        return productoRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarPorId(id);
        if (despachoRepository.existsByDetallesProductoId(id)) {
            throw new ReglaNegocioException("No se puede eliminar el producto porque tiene despachos asociados");
        }
        productoRepository.delete(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscar(String nombre, Long categoriaId, Boolean stockBajo, String orden, String dir) {
        Specification<Producto> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (nombre != null && !nombre.isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }
            if (categoriaId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categoria").get("id"), categoriaId));
            }
            if (Boolean.TRUE.equals(stockBajo)) {
                predicates = cb.and(predicates, cb.le(root.get("stock"), root.get("stockMinimo")));
            }

            return predicates;
        };

        String campoOrden = (orden != null && (orden.equalsIgnoreCase("costoUnitario") || orden.equalsIgnoreCase("costo") || orden.equalsIgnoreCase("stock")))
                ? (orden.equalsIgnoreCase("costo") ? "costoUnitario" : orden)
                : "nombre";

        Sort.Direction direccion = (dir != null && dir.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;

        return productoRepository.findAll(spec, Sort.by(direccion, campoOrden));
    }
}