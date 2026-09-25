package pe.edu.upeu.InventarioBackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.InventarioBackend.entity.Area;
import pe.edu.upeu.InventarioBackend.exeption.RecursoNoEncontradoException;
import pe.edu.upeu.InventarioBackend.exeption.ReglaNegocioException;
import pe.edu.upeu.InventarioBackend.repository.AreaRepository;
import pe.edu.upeu.InventarioBackend.service.service.AreaService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Area> listar() {
        return areaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Area buscarPorId(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Área no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public Area crear(Area area) {
        if (areaRepository.existsByCodigoIgnoreCase(area.getCodigo())) {
            throw new ReglaNegocioException("Ya existe un área con el código: " + area.getCodigo());
        }
        if (areaRepository.existsByNombreIgnoreCase(area.getNombre())) {
            throw new ReglaNegocioException("Ya existe un área con el nombre: " + area.getNombre());
        }
        return areaRepository.save(area);
    }

    @Override
    @Transactional
    public Area actualizar(Long id, Area areaActualizada) {
        Area existente = buscarPorId(id);

        if (areaRepository.existsByCodigoIgnoreCaseAndIdNot(areaActualizada.getCodigo(), id)) {
            throw new ReglaNegocioException("Ya existe otra área con el código: " + areaActualizada.getCodigo());
        }
        if (areaRepository.existsByNombreIgnoreCaseAndIdNot(areaActualizada.getNombre(), id)) {
            throw new ReglaNegocioException("Ya existe otra área con el nombre: " + areaActualizada.getNombre());
        }

        existente.setCodigo(areaActualizada.getCodigo());
        existente.setNombre(areaActualizada.getNombre());
        existente.setResponsable(areaActualizada.getResponsable());
        existente.setEmail(areaActualizada.getEmail());
        existente.setPresupuestoMensual(areaActualizada.getPresupuestoMensual());
        if (areaActualizada.getEstado() != null) {
            existente.setEstado(areaActualizada.getEstado());
        }

        return areaRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Area area = buscarPorId(id);
        areaRepository.delete(area);
    }
}