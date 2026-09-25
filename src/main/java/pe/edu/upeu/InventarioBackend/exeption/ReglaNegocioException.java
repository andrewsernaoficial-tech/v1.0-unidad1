package pe.edu.upeu.InventarioBackend.exeption;

public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String message) {
        super(message);
    }
}