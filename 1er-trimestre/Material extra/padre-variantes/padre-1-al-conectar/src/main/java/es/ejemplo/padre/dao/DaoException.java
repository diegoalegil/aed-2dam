package es.ejemplo.padre.dao;

/**
 * Excepción no comprobada del acceso a datos.
 */
public class DaoException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje y la causa original.
     *
     * @param mensaje texto del error
     * @param causa   excepción original (normalmente una SQLException)
     */
    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
