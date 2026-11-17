package es.ejemplo.cinefest.orm.entity;

// Importa todas las anotaciones de JPA (@Entity, @Table, @Id, @Column...) del paquete jakarta.persistence
import jakarta.persistence.*;

/**
 * Entidad JPA: clase que representa una fila de la tabla salas, es decir, una sala de cine, que pertenece a una sede.
 * Hibernate crea un objeto de esta clase por cada fila; en JDBC lo hacías a mano con rs.getInt(...), rs.getString(...).
 * Cada atributo se corresponde con una columna. En CineFest no hay relaciones entre entidades (@ManyToOne, @OneToMany):
 * las claves foráneas se guardan como simples Integer. La sección 3 de la guía muestra cómo se escribiría una relación.
 */
// @Entity: marca la clase como entidad, para que Hibernate la gestione (también debe estar en persistence.xml)
@Entity
// @Table: nombre de la tabla de SQLite a la que se asocia esta clase
@Table(name = "salas")
public class SalaEntity {
    // @Id: este atributo es la clave primaria (PRIMARY KEY) de la tabla
    @Id
    // @GeneratedValue IDENTITY: el id lo genera SQLite (AUTOINCREMENT); tras el persist, Hibernate lo rellena solo, como getGeneratedKeys en JDBC
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column: enlaza el atributo con la columna id_sala
    @Column(name = "id_sala")
    private Integer idSala;
    // @Column: enlaza el atributo con la columna nombre
    @Column(name = "nombre")
    private String nombre;
    // @Column: enlaza el atributo con la columna aforo
    @Column(name = "aforo")
    private Integer aforo;
    // @Column: columna id_sede; es una clave foránea guardada como Integer simple (sin @ManyToOne, no hay relaciones en CineFest)
    @Column(name = "id_sede")
    private Integer idSede;
    /**
     * Constructor vacío: JPA lo exige para crear los objetos al leer filas. No se debe borrar ni hacerlo privado.
     */
    public SalaEntity() {}
    /**
     * Devuelve id sala.
     *
     * @return valor actual de id sala
     */
    public Integer getIdSala() { return idSala; }
    /**
     * Cambia id sala.
     *
     * @param value nuevo valor de id sala
     */
    public void setIdSala(Integer value) { this.idSala = value; }
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
     * Devuelve aforo.
     *
     * @return valor actual de aforo
     */
    public Integer getAforo() { return aforo; }
    /**
     * Cambia aforo.
     *
     * @param value nuevo valor de aforo
     */
    public void setAforo(Integer value) { this.aforo = value; }
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
}
