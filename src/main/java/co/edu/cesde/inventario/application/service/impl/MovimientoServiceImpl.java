package co.edu.cesde.inventario.application.service.impl;

import co.edu.cesde.inventario.application.service.MovimientoService;
import co.edu.cesde.inventario.application.service.ProductoService;
import co.edu.cesde.inventario.domain.model.Movimiento;
import co.edu.cesde.inventario.domain.model.Producto;
import co.edu.cesde.inventario.domain.model.TipoMovimiento;
import co.edu.cesde.inventario.infrastructure.exception.InsufficientStockException;
import co.edu.cesde.inventario.infrastructure.exception.ResourceNotFoundException;
import co.edu.cesde.inventario.infrastructure.exception.ValidationException;
import co.edu.cesde.inventario.infrastructure.repository.MovimientoRepository;
import co.edu.cesde.inventario.infrastructure.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final ProductoService productoService;

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository,
                                 ProductoRepository productoRepository,
                                 ProductoService productoService) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    @Override
    public Movimiento save(Movimiento movimiento) {
        Producto producto = buscarProductoDelMovimiento(movimiento);

        aplicarMovimiento(producto, movimiento.getTipo(), movimiento.getCantidad());
        productoRepository.save(producto);

        movimiento.setId(null);
        movimiento.setProducto(producto);
        return movimientoRepository.save(movimiento);
    }

    @Override
    public List<Movimiento> findAll() {
        return movimientoRepository.findAll();
    }

    @Override
    public Movimiento findById(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el movimiento con id: " + id));
    }

    @Override
    public void delete(Long id) {
        Movimiento movimiento = findById(id);

        Producto producto = movimiento.getProducto();
        revertirMovimiento(producto, movimiento.getTipo(), movimiento.getCantidad());
        productoRepository.save(producto);

        movimientoRepository.delete(movimiento);
    }

    @Override
    public Movimiento update(Movimiento movimiento) {
        if (movimiento.getId() == null) {
            throw new ValidationException("El id del movimiento es obligatorio para actualizar");
        }
        Movimiento anterior = findById(movimiento.getId());

        Producto productoAnterior = anterior.getProducto();
        revertirMovimiento(productoAnterior, anterior.getTipo(), anterior.getCantidad());
        productoRepository.save(productoAnterior);

        Producto productoNuevo = buscarProductoDelMovimiento(movimiento);
        aplicarMovimiento(productoNuevo, movimiento.getTipo(), movimiento.getCantidad());
        productoRepository.save(productoNuevo);

        movimiento.setProducto(productoNuevo);
        return movimientoRepository.save(movimiento);
    }

    private Producto buscarProductoDelMovimiento(Movimiento movimiento) {
        if (movimiento.getProducto() == null || movimiento.getProducto().getId() == null) {
            throw new ValidationException("Debe indicar el id del producto del movimiento");
        }
        return productoService.findById(movimiento.getProducto().getId());
    }

    private void aplicarMovimiento(Producto producto, TipoMovimiento tipo, int cantidad) {
        if (tipo == TipoMovimiento.ENTRADA) {
            producto.sumarStock(cantidad);
        } else {
            validarStock(producto, cantidad);
            producto.restarStock(cantidad);
        }
    }

    private void revertirMovimiento(Producto producto, TipoMovimiento tipo, int cantidad) {
        if (tipo == TipoMovimiento.ENTRADA) {

            validarStock(producto, cantidad);
            producto.restarStock(cantidad);
        } else {
            producto.sumarStock(cantidad);
        }
    }

    private void validarStock(Producto producto, int cantidad) {
        if (producto.getStock() < cantidad) {
            throw new InsufficientStockException(
                    "Stock insuficiente para el producto " + producto.getNombre()
                            + ". Stock disponible: " + producto.getStock()
                            + ", cantidad solicitada: " + cantidad);
        }
    }
}
