package pe.edu.upeu.InventarioBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 60, message = "El nombre debe tener entre 3 y 60 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no debe exceder los 200 caracteres")
    private String descripcion;

    private Boolean estado;
}