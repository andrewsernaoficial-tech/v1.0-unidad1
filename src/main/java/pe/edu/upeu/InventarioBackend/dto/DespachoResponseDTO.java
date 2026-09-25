package pe.edu.upeu.InventarioBackend.dto;

import lombok.Data;
import pe.edu.upeu.InventarioBackend.enums.EstadoDespacho;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DespachoResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private Long areaId;
    private String areaNombre;
    private String observacion;
    private Integer totalUnidades;
    private BigDecimal montoTotal;
    private EstadoDespacho estado;
    private List<DetalleResponse> detalles;

    @Data
    public static class DetalleResponse {
        private Long id;
        private Long productoId;
        private String productoCodigo;
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal costoUnitario;
        private BigDecimal importe;
    }
}