package es.ejemplo.hotel.modelo;

/** Huesped del hotel (tabla huespedes). Clase mutable con constructores, getters y setters. */
public class Huesped {

    private Integer id;
    private String nombre;
    private String dni;
    private String email;
    private String pais;

    /** Crea un objeto Huesped sin datos. */
    public Huesped() {
    }

    /**
     * Crea un objeto Huesped con todos sus datos.
     *
     * @param id identificador que asigna SQLite (null si aun no esta guardado)
     * @param nombre nombre completo del huesped
     * @param dni documento de identidad, unico en la tabla
     * @param email correo electronico, unico en la tabla
     * @param pais codigo del pais de procedencia, por ejemplo ES o FR
     */
    public Huesped(Integer id, String nombre, String dni, String email, String pais) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.pais = pais;
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
     * Devuelve el valor de nombre.
     *
     * @return nombre completo del huesped
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el valor de nombre.
     *
     * @param nombre nombre completo del huesped
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el valor de dni.
     *
     * @return documento de identidad, unico en la tabla
     */
    public String getDni() {
        return dni;
    }

    /**
     * Cambia el valor de dni.
     *
     * @param dni documento de identidad, unico en la tabla
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Devuelve el valor de email.
     *
     * @return correo electronico, unico en la tabla
     */
    public String getEmail() {
        return email;
    }

    /**
     * Cambia el valor de email.
     *
     * @param email correo electronico, unico en la tabla
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve el valor de pais.
     *
     * @return codigo del pais de procedencia, por ejemplo ES o FR
     */
    public String getPais() {
        return pais;
    }

    /**
     * Cambia el valor de pais.
     *
     * @param pais codigo del pais de procedencia, por ejemplo ES o FR
     */
    public void setPais(String pais) {
        this.pais = pais;
    }
}
