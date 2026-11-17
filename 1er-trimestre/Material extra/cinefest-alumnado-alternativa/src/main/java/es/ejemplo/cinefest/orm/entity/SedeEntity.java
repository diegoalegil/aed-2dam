package es.ejemplo.cinefest.orm.entity;

// Importa todas las anotaciones de JPA (@Entity, @Table, @Id, @Column...) del paquete jakarta.persistence
import jakarta.persistence.*;

/**
 * Entidad JPA: clase que representa una fila de la tabla sedes, es decir, una sede (cine) del festival.
 * Hibernate crea un objeto de esta clase por cada fila; en JDBC lo hacías a mano con rs.getInt(...), rs.getString(...).
 * Cada atributo se corresponde con una columna. En CineFest no hay relaciones entre entidades (@ManyToOne, @OneToMany):
 * las claves foráneas se guardan como simples Integer. La sección 3 de la guía muestra cómo se escribiría una relación.
 */
// @Entity: marca la clase como entidad, para que Hibernate la gestione (también debe estar en persistence.xml)
@Entity
// @Table: nombre de la tabla de SQLite a la que se asocia esta clase
@Table(name = "sedes")
public class SedeEntity {
    // @Id: este atributo es la clave primaria (PRIMARY KEY) de la tabla
    @Id
    // @GeneratedValue IDENTITY: el id lo genera SQLite (AUTOINCREMENT); tras el persist, Hibernate lo rellena solo, como getGeneratedKeys en JDBC
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column: enlaza el atributo con la columna id_sede
    @Column(name = "id_sede")
    private Integer idSede;
    // @Column: enlaza el atributo con la columna nombre
    @Column(name = "nombre")
    private String nombre;
    // @Column: enlaza el atributo con la columna municipio
    @Column(name = "municipio")
    private String municipio;
    /**
     * Constructor vacío: JPA lo exige para crear los objetos al leer filas. No se debe borrar ni hacerlo privado.
     */
    public SedeEntity() {}
    /**
     * Devuelve id sede.
     *
     * @return valor actual de id sede
     */
    public Integer getIdSede() { return idSede; }
    /**
     * Cambia id sede.
     *
     * @param value nuevo valor de id sede
     */
    public void setIdSede(Integer value) { this.idSede = value; }
    /**
     * Devuelve nombre.
     *
     * @return valor actual de nombre
     */
    public String getNombre() { return nombre; }
    /**
     * Cambia nombre.
     *
     * @param value nuevo valor de nombre
     */
    public void setNombre(String value) { this.nombre = value; }
    /**
     * Devuelve municipio.
     *
     * @return valor actual de municipio
     */
    public String getMunicipio() { return municipio; }
    /**
     * Cambia municipio.
     *
     * @param value nuevo valor de municipio
     */
    public void setMunicipio(String value) { this.municipio = value; }
}
