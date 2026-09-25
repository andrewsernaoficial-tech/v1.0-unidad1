package pe.edu.upeu.InventarioBackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.InventarioBackend.dto.DespachoRequestDTO;
import pe.edu.upeu.InventarioBackend.dto.DetalleDespachoRequestDTO;
import pe.edu.upeu.InventarioBackend.dto.ProductoDespachadoDTO;
import pe.edu.upeu.InventarioBackend.entity.Area;
import pe.edu.upeu.InventarioBackend.entity.Despacho;
import pe.edu.upeu.InventarioBackend.entity.DetalleDespacho;
import pe.edu.upeu.InventarioBackend.entity.Producto;
import pe.edu.upeu.InventarioBackend.enums.EstadoDespacho;
import pe.edu.upeu.InventarioBackend.exeption.RecursoNoEncontradoException;
import pe.edu.upeu.InventarioBackend.exeption.ReglaNegocioException;
import pe.edu.upeu.InventarioBackend.repository.AreaRepository;
import pe.edu.upeu.InventarioBackend.repository.DespachoRepository;
import pe.edu.upeu.InventarioBackend.repository.ProductoRepository;
import pe.edu.upeu.InventarioBackend.service.service.DespachoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DespachoServiceImpl implements DespachoService {

    private final DespachoRepository despachoRepository;
    private final AreaRepository areaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Despacho> listar() {
        return despachoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Despacho buscarPorId(Long id) {
        return despachoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Despacho no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Despacho registrar(DespachoRequestDTO dto) {
        Area area = areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Área no encontrada con ID: " + dto.getAreaId()));
        if (!Boolean.TRUE.equals(area.getEstado())) {
            throw new ReglaNegocioException("No se puede despachar a un área inactiva");
        }

        Set<Long> productosProcesados = new HashSet<>();
        for (DetalleDespachoRequestDTO d : dto.getDetalles()) {
            if (!productosProcesados.add(d.getProductoId())) {
                throw new ReglaNegocioException("El despacho no puede contener productos repetidos en el detalle");
            }
        }

        Despacho despacho = new Despacho();
        despacho.setArea(area);
        despacho.setObservacion(dto.getObservacion());
        despacho.setFecha(LocalDateTime.now());
        despacho.setEstado(EstadoDespacho.REGISTRADO);

        int totalUnidades = 0;
        BigDecimal montoTotal = BigDecimal.ZERO;

        for (DetalleDespachoRequestDTO detDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detDto.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + detDto.getProductoId()));

            if (!Boolean.TRUE.equals(producto.getEstado())) {
                throw new ReglaNegocioException("El producto " + producto.getNombre() + " está inactivo");
            }

            if (producto.getStock() < detDto.getCantidad()) {
                throw new ReglaNegocioException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - detDto.getCantidad());

            BigDecimal costoUnitario = producto.getCostoUnitario();
            BigDecimal importe = costoUnitario.multiply(BigDecimal.valueOf(detDto.getCantidad())).setScale(2, RoundingMode.HALF_UP);

            DetalleDespacho detalle = DetalleDespacho.builder()
                    .producto(producto)
                    .cantidad(detDto.getCantidad())
                    .costoUnitario(costoUnitario)
                    .importe(importe)
                    .build();

            despacho.agregarDetalle(detalle);

            totalUnidades += detDto.getCantidad();
            montoTotal = montoTotal.add(importe);
        }

        despacho.setTotalUnidades(totalUnidades);
        despacho.setMontoTotal(montoTotal);

        YearMonth ym = YearMonth.now();
        LocalDateTime inicioMes = ym.atDay(1).atStartOfDay();
        LocalDateTime finMes = ym.atEndOfMonth().atTime(23, 59, 59);

        BigDecimal acumuladoMes = despachoRepository.obtenerMontoAcumuladoMes(area.getId(), EstadoDespacho.REGISTRADO, inicioMes, finMes);
        BigDecimal nuevoConsumo = acumuladoMes.add(montoTotal);

        if (nuevoConsumo.compareTo(area.getPresupuestoMensual()) > 0) {
            throw new ReglaNegocioException("El despacho supera el presupuesto mensual asignado al área");
        }

        return despachoRepository.save(despacho);
    }

    @Override
    @Transactional
    public void anular(Long id) {
        Despacho despacho = buscarPorId(id);

        if (despacho.getEstado() == EstadoDespacho.ANULADO) {
            throw new ReglaNegocioException("El despacho ya se encuentra anulado");
        }

        for (DetalleDespacho detalle : despacho.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
        }

        despacho.setEstado(EstadoDespacho.ANULADO);
        despachoRepository.save(despacho);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDespachadoDTO> obtenerReporteProductosDespachados(String periodo, Long categoriaId) {
        YearMonth ym = YearMonth.parse(periodo, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDateTime inicio = ym.atDay(1).atStartOfDay();
        LocalDateTime fin = ym.atEndOfMonth().atTime(23, 59, 59);

        return despachoRepository.obtenerReporteProductosDespachados(inicio, fin, categoriaId);
    }
}