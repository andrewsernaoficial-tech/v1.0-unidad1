package pe.edu.upeu.InventarioBackend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class DespachoRequestDTO {
    @NotNull(message = "El área es obligatoria")
    private Long areaId;

    @Size(max = 200, message = "La observación no debe exceder los 200 caracteres")
    private String observacion;

    @NotEmpty(message = "El despacho debe incluir al menos un producto")
    private List<DetalleDespachoRequestDTO> detalles;
}