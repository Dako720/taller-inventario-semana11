package co.edu.cesde.inventario.application.service;

import co.edu.cesde.inventario.domain.model.Producto;

import java.util.List;

public interface ProductoService {

    Producto save(Producto producto);

    List<Producto> findAll();

    Producto findById(Long id);

    void delete(Long id);

    Producto update(Producto producto);
}
