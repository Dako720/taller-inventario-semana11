package co.edu.cesde.inventario.application.service.impl;

import co.edu.cesde.inventario.application.service.ProductoService;
import co.edu.cesde.inventario.domain.model.Producto;
import co.edu.cesde.inventario.infrastructure.exception.DuplicateResourceException;
import co.edu.cesde.inventario.infrastructure.exception.ResourceNotFoundException;
import co.edu.cesde.inventario.infrastructure.exception.ValidationException;
import co.edu.cesde.inventario.infrastructure.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto save(Producto producto) {
        Optional<Producto> existente = productoRepository.findByNombre(producto.getNombre());
        if (existente.isPresent()) {
            throw new DuplicateResourceException(
                    "Ya existe un producto con el nombre: " + producto.getNombre());
        }

        producto.setId(null);
        return productoRepository.save(producto);
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el producto con id: " + id));
    }

    @Override
    public void delete(Long id) {
        Producto producto = findById(id);
        productoRepository.delete(producto);
    }

    @Override
    public Producto update(Producto producto) {
        if (producto.getId() == null) {
            throw new ValidationException("El id del producto es obligatorio para actualizar");
        }

        findById(producto.getId());

        Optional<Producto> existente = productoRepository.findByNombre(producto.getNombre());
        if (existente.isPresent() && !existente.get().getId().equals(producto.getId())) {
            throw new DuplicateResourceException(
                    "Ya existe un producto con el nombre: " + producto.getNombre());
        }
        return productoRepository.save(producto);
    }
}
