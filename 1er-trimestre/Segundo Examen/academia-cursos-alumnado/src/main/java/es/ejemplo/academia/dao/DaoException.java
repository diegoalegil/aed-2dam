package es.ejemplo.academia.dao;

/**
 * Excepción del proyecto para cualquier fallo de acceso a datos. Es no comprobada
 * (hereda de RuntimeException), así que los métodos no necesitan "throws".
 */
public class DaoException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje y la causa original.
     *
     * @param mensaje explicación del fallo
     * @param causa   excepción original (normalmente la SQLException), puede ser null
     */
    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
