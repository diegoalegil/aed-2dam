package es.ejemplo.banco.orm.repository;

/**
 * Error de acceso a datos de la capa ORM. Conserva siempre la causa original.
 */
public class RepositoryException extends RuntimeException {

    public RepositoryException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
