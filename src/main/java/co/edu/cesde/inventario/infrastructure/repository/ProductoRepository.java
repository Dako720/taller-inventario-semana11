package co.edu.cesde.inventario.infrastructure.repository;

import co.edu.cesde.inventario.domain.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByNombre(String nombre);
}
