package pe.edu.upeu.InventarioBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.InventarioBackend.entity.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    List<Producto> findByCategoriaId(Long categoriaId);

    long countByCategoriaId(Long categoriaId);

    @Query("SELECT p FROM Producto p WHERE " +
            "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
            "(:stockBajo IS NULL OR " +
            " (:stockBajo = true AND p.stock <= p.stockMinimo) OR " +
            " (:stockBajo = false AND p.stock > p.stockMinimo))")
    List<Producto> buscarProductosFiltros(@Param("nombre") String nombre,
                                          @Param("categoriaId") Long categoriaId,
                                          @Param("stockBajo") Boolean stockBajo);
}