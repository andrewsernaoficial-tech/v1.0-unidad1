package pe.edu.upeu.InventarioBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.InventarioBackend.dto.ProductoDespachadoDTO;
import pe.edu.upeu.InventarioBackend.entity.Despacho;
import pe.edu.upeu.InventarioBackend.enums.EstadoDespacho;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    boolean existsByDetallesProductoId(Long productoId);

    @Query("SELECT COALESCE(SUM(d.montoTotal), 0) FROM Despacho d " +
            "WHERE d.area.id = :areaId " +
            "AND d.estado = :estado " +
            "AND d.fecha >= :inicioMes AND d.fecha <= :finMes")
    BigDecimal obtenerMontoAcumuladoMes(@Param("areaId") Long areaId,
                                        @Param("estado") EstadoDespacho estado,
                                        @Param("inicioMes") LocalDateTime inicioMes,
                                        @Param("finMes") LocalDateTime finMes);

    @Query("SELECT p.codigo AS codigo, p.nombre AS producto, " +
            "SUM(dt.cantidad) AS unidadesDespachadas, SUM(dt.importe) AS montoTotal " +
            "FROM DetalleDespacho dt " +
            "JOIN dt.despacho d " +
            "JOIN dt.producto p " +
            "WHERE d.estado = 'REGISTRADO' " +
            "AND d.fecha >= :inicio AND d.fecha <= :fin " +
            "AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId) " +
            "GROUP BY p.codigo, p.nombre " +
            "ORDER BY SUM(dt.cantidad) DESC, p.codigo ASC")
    List<ProductoDespachadoDTO> obtenerReporteProductosDespachados(@Param("inicio") LocalDateTime inicio,
                                                                   @Param("fin") LocalDateTime fin,
                                                                   @Param("categoriaId") Long categoriaId);
}