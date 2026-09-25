package pe.edu.upeu.InventarioBackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoriaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
}