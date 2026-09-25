package pe.edu.upeu.InventarioBackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.InventarioBackend.dto.*;
import pe.edu.upeu.InventarioBackend.entity.*;
import pe.edu.upeu.InventarioBackend.enums.EstadoDespacho;
import pe.edu.upeu.InventarioBackend.exeption.RecursoNoEncontradoException;
import pe.edu.upeu.InventarioBackend.exeption.ReglaNegocioException;
import pe.edu.upeu.InventarioBackend.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DespachoServiceImpl {

    private final DespachoRepository despachoRepository;
    private final AreaRepository areaRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public Despacho registrarDespacho(DespachoRequestDTO dto) {
        Area area = areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Área no encontrada"));
        if (!Boolean.TRUE.equals(area.getEstado())) {
            throw new ReglaNegocioException("No se puede registrar despacho: El área no está activa");
        }

        Set<Long> productosUnicos = new HashSet<>();
        for (DetalleDespachoRequestDTO d : dto.getDetalles()) {
            if (!productosUnicos.add(d.getProductoId())) {
                throw new ReglaNegocioException("Un despacho no puede contener productos repetidos");
            }
        }

        Despacho despacho = new Despacho();
        despacho.setArea(area);
        despacho.setObservacion(dto.getObservacion());
        despacho.setFecha(LocalDateTime.now());
        despacho.setEstado(EstadoDespacho.REGISTRADO);

        int totalUnidades = 0;
        BigDecimal montoTotal = BigDecimal.ZERO;

        for (DetalleDespachoRequestDTO itemDTO : dto.getDetalles()) {
            Producto producto = productoRepository.findById(itemDTO.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + itemDTO.getProductoId()));

            if (!Boolean.TRUE.equals(producto.getEstado())) {
                throw new ReglaNegocioException("El producto " + producto.getNombre() + " está inactivo");
            }

            if (producto.getStock() < itemDTO.getCantidad()) {
                throw new ReglaNegocioException("Stock insuficiente para " + producto.getNombre() +
                        ". Disponible: " + producto.getStock() + ", solicitado: " + itemDTO.getCantidad());
            }

            producto.setStock(producto.getStock() - itemDTO.getCantidad());

            BigDecimal costoUnitario = producto.getCostoUnitario();
            BigDecimal importe = costoUnitario.multiply(BigDecimal.valueOf(itemDTO.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleDespacho detalle = DetalleDespacho.builder()
                    .producto(producto)
                    .cantidad(itemDTO.getCantidad())
                    .costoUnitario(costoUnitario)
                    .importe(importe)
                    .build();

            despacho.agregarDetalle(detalle);

            totalUnidades += itemDTO.getCantidad();
            montoTotal = montoTotal.add(importe);
        }

        despacho.setTotalUnidades(totalUnidades);
        despacho.setMontoTotal(montoTotal);

        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(23, 59, 59);

        BigDecimal consumidoMes = despachoRepository.obtenerMontoAcumuladoMes(area.getId(), EstadoDespacho.REGISTRADO, inicioMes, finMes);
        BigDecimal nuevoAcumulado = consumidoMes.add(montoTotal);

        if (nuevoAcumulado.compareTo(area.getPresupuestoMensual()) > 0) {
            throw new ReglaNegocioException("El despacho supera el presupuesto mensual del área");
        }

        return despachoRepository.save(despacho);
    }

    @Transactional
    public void anularDespacho(Long id) {
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Despacho no encontrado"));

        if (despacho.getEstado() == EstadoDespacho.ANULADO) {
            throw new ReglaNegocioException("El despacho ya se encuentra anulado");
        }

        for (DetalleDespacho detalle : despacho.getDetalles()) {
            Producto prod = detalle.getProducto();
            prod.setStock(prod.getStock() + detalle.getCantidad());
        }

        despacho.setEstado(EstadoDespacho.ANULADO);
        despachoRepository.save(despacho);
    }
}