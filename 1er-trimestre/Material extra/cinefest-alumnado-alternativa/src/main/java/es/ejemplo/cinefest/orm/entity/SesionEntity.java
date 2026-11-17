package es.ejemplo.cinefest.orm.entity;

// Importa todas las anotaciones de JPA (@Entity, @Table, @Id, @Column...) del paquete jakarta.persistence
import jakarta.persistence.*;

/**
 * Entidad JPA: clase que representa una fila de la tabla sesiones, es decir, una sesión: una película proyectada en una sala a una fecha y hora.
 * Hibernate crea un objeto de esta clase por cada fila; en JDBC lo hacías a mano con rs.getInt(...), rs.getString(...).
 * Cada atributo se corresponde con una columna. En CineFest no hay relaciones entre entidades (@ManyToOne, @OneToMany):
 * las claves foráneas se guardan como simples Integer. La sección 3 de la guía muestra cómo se escribiría una relación.
 */
// @Entity: marca la clase como entidad, para que Hibernate la gestione (también debe estar en persistence.xml)
@Entity
// @Table: nombre de la tabla de SQLite a la que se asocia esta clase
@Table(name = "sesiones")
public class SesionEntity {
    // @Id: este atributo es la clave primaria (PRIMARY KEY) de la tabla
    @Id
    // @GeneratedValue IDENTITY: el id lo genera SQLite (AUTOINCREMENT); tras el persist, Hibernate lo rellena solo, como getGeneratedKeys en JDBC
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column: columna id_sesion; es una clave foránea guardada como Integer simple (sin @ManyToOne, no hay relaciones en CineFest)
    @Column(name = "id_sesion")
    private Integer idSesion;
    // @Column: columna id_pelicula; es una clave foránea guardada como Integer simple (sin @ManyToOne, no hay relaciones en CineFest)
    @Column(name = "id_pelicula")
    private Integer idPelicula;
    // @Column: columna id_sala; es una clave foránea guardada como Integer simple (sin @ManyToOne, no hay relaciones en CineFest)
    @Column(name = "id_sala")
    private Integer idSala;
    // @Column: enlaza el atributo con la columna fecha
    @Column(name = "fecha")
    private String fecha;
    // @Column: enlaza el atributo con la columna hora
    @Column(name = "hora")
    private String hora;
    // @Column: enlaza el atributo con la columna precio
    @Column(name = "precio")
    private Double precio;
    // @Column: enlaza el atributo con la columna estado
    @Column(name = "estado")
    private String estado;

    /**
     * Constructor vacío: JPA lo exige para crear los objetos al leer filas. No se debe borrar ni hacerlo privado.
     */
    public SesionEntity() {
    }

    /**
     * Devuelve id sesion.
     *
     * @return valor actual de id sesion
     */
    public Integer getIdSesion() {
        return idSesion;
    }

    /**
     * Cambia id sesion.
     *
     * @param value nuevo valor de id sesion
     */
    public void setIdSesion(Integer value) {
        this.idSesion = value;
    }

    /**
     * Devuelve id pelicula.
     *
     * @return valor actual de id pelicula
     */
    public Integer getIdPelicula() {
        return idPelicula;
    }

    /**
     * Cambia id pelicula.
     *
     * @param value nuevo valor de id pelicula
     */
    public void setIdPelicula(Integer value) {
        this.idPelicula = value;
    }

    /**
     * Devuelve id sala.
     *
     * @return valor actual de id sala
     */
    public Integer getIdSala() {
        return idSala;
    }

    /**
     * Cambia id sala.
     *
     * @param value nuevo valor de id sala
     */
    public void setIdSala(Integer value) {
        this.idSala = value;
    }

    /**
     * Devuelve fecha.
     *
     * @return valor actual de fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Cambia fecha.
     *
     * @param value nuevo valor de fecha
     */
    public void setFecha(String value) {
        this.fecha = value;
    }

    /**
     * Devuelve hora.
     *
     * @return valor actual de hora
     */
    public String getHora() {
        return hora;
    }

    /**
     * Cambia hora.
     *
     * @param value nuevo valor de hora
     */
    public void setHora(String value) {
        this.hora = value;
    }

    /**
     * Devuelve precio.
     *
     * @return valor actual de precio
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Cambia precio.
     *
     * @param value nuevo valor de precio
     */
    public void setPrecio(Double value) {
        this.precio = value;
    }

    /**
     * Devuelve estado.
     *
     * @return valor actual de estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Cambia estado.
     *
     * @param value nuevo valor de estado
     */
    public void setEstado(String value) {
        this.estado = value;
    }
}
