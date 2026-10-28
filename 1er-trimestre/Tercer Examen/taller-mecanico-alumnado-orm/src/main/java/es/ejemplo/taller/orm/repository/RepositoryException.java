package es.ejemplo.taller.orm.repository;

// Guía §14: la usa write(...) para envolver los errores de escritura (la guía no la define)
public class RepositoryException extends RuntimeException {

    public RepositoryException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
