package es.ejemplo.hotel.dao;

/** Excepcion no comprobada para los errores de acceso a datos. */
public class DaoException extends RuntimeException {

    /**
     * Crea la excepcion con un mensaje y la causa original.
     *
     * @param mensaje descripcion del error
     * @param causa   excepcion que lo provoca
     */
    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
