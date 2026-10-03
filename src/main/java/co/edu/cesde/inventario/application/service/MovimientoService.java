package co.edu.cesde.inventario.application.service;

import co.edu.cesde.inventario.domain.model.Movimiento;

import java.util.List;

public interface MovimientoService {

    Movimiento save(Movimiento movimiento);

    List<Movimiento> findAll();

    Movimiento findById(Long id);

    void delete(Long id);

    Movimiento update(Movimiento movimiento);
}
