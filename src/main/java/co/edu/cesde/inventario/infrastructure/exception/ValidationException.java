package co.edu.cesde.inventario.infrastructure.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String mensaje) {
        super(mensaje);
    }
}
