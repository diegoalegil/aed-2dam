package es.ejemplo.anime.exception;

/** Error no recuperable de acceso a datos o de validación. */
public class AplicationException extends RuntimeException {
    public AplicationException(String mensaje) { super(mensaje); }
    public AplicationException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
