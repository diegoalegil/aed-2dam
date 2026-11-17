package es.ejemplo.hotel.modelo;

/** Reserva de una habitacion por un huesped (tabla reservas). Clase mutable con constructores, getters y setters. */
public class Reserva {

    private Integer id;
    private Integer idHuesped;
    private Integer idHabitacion;
    private String fechaEntrada;
    private Integer noches;
    private String estado;

    /** Crea un objeto Reserva sin datos. */
    public Reserva() {
    }

    /**
     * Crea un objeto Reserva con todos sus datos.
     *
     * @param id identificador que asigna SQLite (null si aun no esta guardada)
     * @param idHuesped identificador del huesped que reserva
     * @param idHabitacion identificador de la habitacion reservada
     * @param fechaEntrada fecha de entrada en formato ISO AAAA-MM-DD
     * @param noches numero de noches (mayor que 0)
     * @param estado estado de la reserva: CONFIRMADA, CANCELADA o FINALIZADA
     */
    public Reserva(Integer id, Integer idHuesped, Integer idHabitacion, String fechaEntrada, Integer noches, String estado) {
        this.id = id;
        this.idHuesped = idHuesped;
        this.idHabitacion = idHabitacion;
        this.fechaEntrada = fechaEntrada;
        this.noches = noches;
        this.estado = estado;
    }

    /**
     * Devuelve el valor de id.
     *
     * @return identificador que asigna SQLite (null si aun no esta guardada)
     */
    public Integer getId() {
        return id;
    }

    /**
     * Cambia el valor de id.
     *
     * @param id identificador que asigna SQLite (null si aun no esta guardada)
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Devuelve el valor de idHuesped.
     *
     * @return identificador del huesped que reserva
     */
    public Integer getIdHuesped() {
        return idHuesped;
    }

    /**
     * Cambia el valor de idHuesped.
     *
     * @param idHuesped identificador del huesped que reserva
     */
    public void setIdHuesped(Integer idHuesped) {
        this.idHuesped = idHuesped;
    }

    /**
     * Devuelve el valor de idHabitacion.
     *
     * @return identificador de la habitacion reservada
     */
    public Integer getIdHabitacion() {
        return idHabitacion;
    }

    /**
     * Cambia el valor de idHabitacion.
     *
     * @param idHabitacion identificador de la habitacion reservada
     */
    public void setIdHabitacion(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    /**
     * Devuelve el valor de fechaEntrada.
     *
     * @return fecha de entrada en formato ISO AAAA-MM-DD
     */
    public String getFechaEntrada() {
        return fechaEntrada;
    }

    /**
     * Cambia el valor de fechaEntrada.
     *
     * @param fechaEntrada fecha de entrada en formato ISO AAAA-MM-DD
     */
    public void setFechaEntrada(String fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    /**
     * Devuelve el valor de noches.
     *
     * @return numero de noches (mayor que 0)
     */
    public Integer getNoches() {
        return noches;
    }

    /**
     * Cambia el valor de noches.
     *
     * @param noches numero de noches (mayor que 0)
     */
    public void setNoches(Integer noches) {
        this.noches = noches;
    }

    /**
     * Devuelve el valor de estado.
     *
     * @return estado de la reserva: CONFIRMADA, CANCELADA o FINALIZADA
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Cambia el valor de estado.
     *
     * @param estado estado de la reserva: CONFIRMADA, CANCELADA o FINALIZADA
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
