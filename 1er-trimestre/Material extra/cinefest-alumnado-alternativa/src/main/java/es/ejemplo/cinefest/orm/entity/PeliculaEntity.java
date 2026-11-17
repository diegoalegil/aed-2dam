package es.ejemplo.cinefest.orm.entity;

// Importa todas las anotaciones de JPA (@Entity, @Table, @Id, @Column...) del paquete jakarta.persistence
import jakarta.persistence.*;

/**
 * Entidad JPA: clase que representa una fila de la tabla peliculas, es decir, una película del catálogo.
 * Hibernate crea un objeto de esta clase por cada fila; en JDBC lo hacías a mano con rs.getInt(...), rs.getString(...).
 * Cada atributo se corresponde con una columna. En CineFest no hay relaciones entre entidades (@ManyToOne, @OneToMany):
 * las claves foráneas se guardan como simples Integer. La sección 3 de la guía muestra cómo se escribiría una relación.
 */
// @Entity: marca la clase como entidad, para que Hibernate la gestione (también debe estar en persistence.xml)
@Entity
// @Table: nombre de la tabla de SQLite a la que se asocia esta clase
@Table(name = "peliculas")
public class PeliculaEntity {
    // @Id: este atributo es la clave primaria (PRIMARY KEY) de la tabla
    @Id
    // @GeneratedValue IDENTITY: el id lo genera SQLite (AUTOINCREMENT); tras el persist, Hibernate lo rellena solo, como getGeneratedKeys en JDBC
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column: enlaza el atributo con la columna id_pelicula
    @Column(name = "id_pelicula")
    private Integer idPelicula;
    // @Column: enlaza el atributo con la columna titulo
    @Column(name = "titulo")
    private String titulo;
    // @Column: enlaza el atributo con la columna genero
    @Column(name = "genero")
    private String genero;
    // @Column: enlaza el atributo con la columna duracion_min (se pone el nombre porque en Java usamos camelCase y en SQL snake_case)
    @Column(name = "duracion_min")
    private Integer duracionMin;
    // @Column: enlaza el atributo con la columna pais
    @Column(name = "pais")
    private String pais;
    // @Column: enlaza el atributo con la columna anio
    @Column(name = "anio")
    private Integer anio;
    /**
     * Constructor vacío: JPA lo exige para crear los objetos al leer filas. No se debe borrar ni hacerlo privado.
     */
    public PeliculaEntity() {}
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
     * Devuelve titulo.
     *
     * @return valor actual de titulo
     */
    public String getTitulo() { return titulo; }
    /**
     * Cambia titulo.
     *
     * @param value nuevo valor de titulo
     */
    public void setTitulo(String value) { this.titulo = value; }
    /**
     * Devuelve genero.
     *
     * @return valor actual de genero
     */
    public String getGenero() { return genero; }
    /**
     * Cambia genero.
     *
     * @param value nuevo valor de genero
     */
    public void setGenero(String value) { this.genero = value; }
    /**
     * Devuelve duracion min.
     *
     * @return valor actual de duracion min
     */
    public Integer getDuracionMin() { return duracionMin; }
    /**
     * Cambia duracion min.
     *
     * @param value nuevo valor de duracion min
     */
    public void setDuracionMin(Integer value) { this.duracionMin = value; }
    /**
     * Devuelve pais.
     *
     * @return valor actual de pais
     */
    public String getPais() { return pais; }
    /**
     * Cambia pais.
     *
     * @param value nuevo valor de pais
     */
    public void setPais(String value) { this.pais = value; }
    /**
     * Devuelve anio.
     *
     * @return valor actual de anio
     */
    public Integer getAnio() { return anio; }
    /**
     * Cambia anio.
     *
     * @param value nuevo valor de anio
     */
    public void setAnio(Integer value) { this.anio = value; }
}
