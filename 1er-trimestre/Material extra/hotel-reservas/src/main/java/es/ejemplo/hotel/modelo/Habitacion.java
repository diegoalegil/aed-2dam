package es.ejemplo.hotel.modelo;

import java.math.BigDecimal;

/** Habitacion del hotel (tabla habitaciones). Clase mutable con constructores, getters y setters. */
public class Habitacion {

    private Integer id;
    private String numero;
    private String tipo;
    private Integer planta;
    private BigDecimal precioNoche;

    /** Crea un objeto Habitacion sin datos. */
    public Habitacion() {
    }

    /**
     * Crea un objeto Habitacion con todos sus datos.
     *
     * @param id identificador que asigna SQLite (null si aun no esta guardada)
     * @param numero numero de la habitacion como texto, por ejemplo 101, unico en la tabla
     * @param tipo tipo de habitacion: INDIVIDUAL, DOBLE o SUITE
     * @param planta planta en la que esta la habitacion (0 o mas)
     * @param precioNoche precio de una noche en euros (mayor que 0)
     */
    public Habitacion(Integer id, String numero, String tipo, Integer planta, BigDecimal precioNoche) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.planta = planta;
        this.precioNoche = precioNoche;
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
     * Devuelve el valor de numero.
     *
     * @return numero de la habitacion como texto, por ejemplo 101, unico en la tabla
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Cambia el valor de numero.
     *
     * @param numero numero de la habitacion como texto, por ejemplo 101, unico en la tabla
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * Devuelve el valor de tipo.
     *
     * @return tipo de habitacion: INDIVIDUAL, DOBLE o SUITE
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Cambia el valor de tipo.
     *
     * @param tipo tipo de habitacion: INDIVIDUAL, DOBLE o SUITE
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Devuelve el valor de planta.
     *
     * @return planta en la que esta la habitacion (0 o mas)
     */
    public Integer getPlanta() {
        return planta;
    }

    /**
     * Cambia el valor de planta.
     *
     * @param planta planta en la que esta la habitacion (0 o mas)
     */
    public void setPlanta(Integer planta) {
        this.planta = planta;
    }

    /**
     * Devuelve el valor de precioNoche.
     *
     * @return precio de una noche en euros (mayor que 0)
     */
    public BigDecimal getPrecioNoche() {
        return precioNoche;
    }

    /**
     * Cambia el valor de precioNoche.
     *
     * @param precioNoche precio de una noche en euros (mayor que 0)
     */
    public void setPrecioNoche(BigDecimal precioNoche) {
        this.precioNoche = precioNoche;
    }
}
