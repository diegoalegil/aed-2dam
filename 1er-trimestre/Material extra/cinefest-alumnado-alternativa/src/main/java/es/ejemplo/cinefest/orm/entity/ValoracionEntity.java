package es.ejemplo.cinefest.orm.entity;

// Importa todas las anotaciones de JPA (@Entity, @Table, @Id, @Column...) del paquete jakarta.persistence
import jakarta.persistence.*;

/**
 * Entidad JPA: clase que representa una fila de la tabla valoraciones, es decir, la valoración que un espectador hace de una película.
 * Hibernate crea un objeto de esta clase por cada fila; en JDBC lo hacías a mano con rs.getInt(...), rs.getString(...).
 * Cada atributo se corresponde con una columna. En CineFest no hay relaciones entre entidades (@ManyToOne, @OneToMany):
 * las claves foráneas se guardan como simples Integer. La sección 3 de la guía muestra cómo se escribiría una relación.
 */
// @Entity: marca la clase como entidad, para que Hibernate la gestione (también debe estar en persistence.xml)
@Entity
// @Table: nombre de la tabla de SQLite a la que se asocia esta clase
@Table(name = "valoraciones")
public class ValoracionEntity {
    // @Id: este atributo es la clave primaria (PRIMARY KEY) de la tabla
    @Id
    // @GeneratedValue IDENTITY: el id lo genera SQLite (AUTOINCREMENT); tras el persist, Hibernate lo rellena solo, como getGeneratedKeys en JDBC
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column: columna id_valoracion; es una clave foránea guardada como Integer simple (sin @ManyToOne, no hay relaciones en CineFest)
    @Column(name = "id_valoracion")
    private Integer idValoracion;
    // @Column: columna id_pelicula; es una clave foránea guardada como Integer simple (sin @ManyToOne, no hay relaciones en CineFest)
    @Column(name = "id_pelicula")
    private Integer idPelicula;
    // @Column: columna id_espectador; es una clave foránea guardada como Integer simple (sin @ManyToOne, no hay relaciones en CineFest)
    @Column(name = "id_espectador")
    private Integer idEspectador;
    // @Column: enlaza el atributo con la columna puntuacion
    @Column(name = "puntuacion")
    private Integer puntuacion;
    // @Column: enlaza el atributo con la columna comentario
    @Column(name = "comentario")
    private String comentario;
    // @Column: enlaza el atributo con la columna fecha
    @Column(name = "fecha")
    private String fecha;
    /**
     * Constructor vacío: JPA lo exige para crear los objetos al leer filas. No se debe borrar ni hacerlo privado.
     */
    public ValoracionEntity() {}
    /**
     * Devuelve id valoracion.
     *
     * @return valor actual de id valoracion
     */
    public Integer getIdValoracion() { return idValoracion; }
    /**
     * Cambia id valoracion.
     *
     * @param value nuevo valor de id valoracion
     */
    public void setIdValoracion(Integer value) { this.idValoracion = value; }
    /**
     * Devuelve id pelicula.
     *
     * @return valor actual de id pelicula
     */
    public Integer getIdPelicula() { return idPelicula; }
    /**
     * Cambia id pelicula.
     *
     * @param value nuevo valor de id pelicula
     */
    public void setIdPelicula(Integer value) { this.idPelicula = value; }
    /**
     * Devuelve id espectador.
     *
     * @return valor actual de id espectador
     */
    public Integer getIdEspectador() { return idEspectador; }
    /**
     * Cambia id espectador.
     *
     * @param value nuevo valor de id espectador
     */
    public void setIdEspectador(Integer value) { this.idEspectador = value; }
    /**
     * Devuelve puntuacion.
     *
     * @return valor actual de puntuacion
     */
    public Integer getPuntuacion() { return puntuacion; }
    /**
     * Cambia puntuacion.
     *
     * @param value nuevo valor de puntuacion
     */
    public void setPuntuacion(Integer value) { this.puntuacion = value; }
    /**
     * Devuelve comentario.
     *
     * @return valor actual de comentario
     */
    public String getComentario() { return comentario; }
    /**
     * Cambia comentario.
     *
     * @param value nuevo valor de comentario
     */
    public void setComentario(String value) { this.comentario = value; }
    /**
     * Devuelve fecha.
     *
     * @return valor actual de fecha
     */
    public String getFecha() { return fecha; }
    /**
     * Cambia fecha.
     *
     * @param value nuevo valor de fecha
     */
    public void setFecha(String value) { this.fecha = value; }
}
