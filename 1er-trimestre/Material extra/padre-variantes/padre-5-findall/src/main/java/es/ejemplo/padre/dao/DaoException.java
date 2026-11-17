package es.ejemplo.padre.dao;

/**
 * Excepción no comprobada para los errores de acceso a la base de datos.
 */
public class DaoException extends RuntimeException {

    /**
     * Crea la excepción guardando el error original.
     *
     * @param mensaje explicación del error
     * @param causa   error original, normalmente un SQLException
     */
    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
