package pe.edu.upeu.InventarioBackend.service.generic;

import java.util.List;

public interface CrudService<T, ID> {
    List<T> listar();
    T buscarPorId(ID id);
    T crear(T entidad);
    T actualizar(ID id, T entidad);
    void eliminar(ID id);
}