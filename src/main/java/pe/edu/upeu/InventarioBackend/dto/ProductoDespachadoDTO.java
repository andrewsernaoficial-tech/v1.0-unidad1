package pe.edu.upeu.InventarioBackend.dto;

import java.math.BigDecimal;

public interface ProductoDespachadoDTO {
    String getCodigo();
    String getProducto();
    Long getUnidadesDespachadas();
    BigDecimal getMontoTotal();
}