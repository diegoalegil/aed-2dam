package es.ejemplo.hotel.modelo;

import java.math.BigDecimal;

/** Consumo cargado a una reserva: minibar, restaurante, spa... (tabla consumos). Clase mutable con constructores, getters y setters. */
public class Consumo {

    private Integer id;
    private Integer idReserva;
    private String concepto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private String fecha;

    /** Crea un objeto Consumo sin datos. */
    public Consumo() {
    }

    /**
     * Crea un objeto Consumo con todos sus datos.
     *
     * @param id identificador que asigna SQLite (null si aun no esta guardado)
     * @param idReserva identificador de la reserva a la que se carga
     * @param concepto descripcion del consumo
     * @param cantidad unidades consumidas (mayor que 0)
     * @param precioUnitario precio de una unidad en euros (0 o mas)
     * @param fecha fecha del consumo en formato ISO AAAA-MM-DD
     */
    public Consumo(Integer id, Integer idReserva, String concepto, Integer cantidad, BigDecimal precioUnitario, String fecha) {
        this.id = id;
        this.idReserva = idReserva;
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fecha = fecha;
    }

    /**
     * Devuelve el valor de id.
     *
     * @return identificador que asigna SQLite (null si aun no esta guardado)
     */
    public Integer getId() {
        return id;
    }

    /**
     * Cambia el valor de id.
     *
     * @param id identificador que asigna SQLite (null si aun no esta guardado)
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Devuelve el valor de idReserva.
     *
     * @return identificador de la reserva a la que se carga
     */
    public Integer getIdReserva() {
        return idReserva;
    }

    /**
     * Cambia el valor de idReserva.
     *
     * @param idReserva identificador de la reserva a la que se carga
     */
    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    /**
     * Devuelve el valor de concepto.
     *
     * @return descripcion del consumo
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * Cambia el valor de concepto.
     *
     * @param concepto descripcion del consumo
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    /**
     * Devuelve el valor de cantidad.
     *
     * @return unidades consumidas (mayor que 0)
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Cambia el valor de cantidad.
     *
     * @param cantidad unidades consumidas (mayor que 0)
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Devuelve el valor de precioUnitario.
     *
     * @return precio de una unidad en euros (0 o mas)
     */
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Cambia el valor de precioUnitario.
     *
     * @param precioUnitario precio de una unidad en euros (0 o mas)
     */
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * Devuelve el valor de fecha.
     *
     * @return fecha del consumo en formato ISO AAAA-MM-DD
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Cambia el valor de fecha.
     *
     * @param fecha fecha del consumo en formato ISO AAAA-MM-DD
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
