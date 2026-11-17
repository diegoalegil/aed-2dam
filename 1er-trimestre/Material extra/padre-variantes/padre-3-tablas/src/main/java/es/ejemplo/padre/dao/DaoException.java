package es.ejemplo.padre.dao;

/**
 * Excepción del acceso a datos. Es no comprobada: no hace falta poner throws.
 */
public class DaoException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje y la causa original.
     *
     * @param mensaje explicación del error
     * @param causa   excepción original (por ejemplo una SQLException), o null si no hay
     */
    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
