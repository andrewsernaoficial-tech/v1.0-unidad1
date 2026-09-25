package pe.edu.upeu.InventarioBackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.InventarioBackend.entity.Categoria;
import pe.edu.upeu.InventarioBackend.exeption.RecursoNoEncontradoException;
import pe.edu.upeu.InventarioBackend.exeption.ReglaNegocioException;
import pe.edu.upeu.InventarioBackend.repository.CategoriaRepository;
import pe.edu.upeu.InventarioBackend.repository.ProductoRepository;
import pe.edu.upeu.InventarioBackend.service.service.CategoriaService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public Categoria crear(Categoria categoria) {
        String nombreLimpio = categoria.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new ReglaNegocioException("Ya existe una categoría con el nombre: " + nombreLimpio);
        }
        categoria.setNombre(nombreLimpio);
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        Categoria existente = buscarPorId(id);
        String nombreLimpio = categoriaActualizada.getNombre().trim();

        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombreLimpio, id)) {
            throw new ReglaNegocioException("Ya existe otra categoría con el nombre: " + nombreLimpio);
        }

        existente.setNombre(nombreLimpio);
        existente.setDescripcion(categoriaActualizada.getDescripcion());
        if (categoriaActualizada.getEstado() != null) {
            existente.setEstado(categoriaActualizada.getEstado());
        }

        return categoriaRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
        if (productoRepository.countByCategoriaId(id) > 0) {
            throw new ReglaNegocioException("No se puede eliminar la categoría porque tiene productos asociados");
        }
        categoriaRepository.delete(categoria);
    }
}