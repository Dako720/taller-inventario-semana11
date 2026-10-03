package co.edu.cesde.inventario.infrastructure.repository;

import co.edu.cesde.inventario.domain.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
}
