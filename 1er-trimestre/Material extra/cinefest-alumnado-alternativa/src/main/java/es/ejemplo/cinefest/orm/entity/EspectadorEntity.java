package es.ejemplo.cinefest.orm.entity;

// Importa todas las anotaciones de JPA (@Entity, @Table, @Id, @Column...) del paquete jakarta.persistence
import jakarta.persistence.*;

/**
 * Entidad JPA: clase que representa una fila de la tabla espectadores, es decir, un espectador registrado.
 * Hibernate crea un objeto de esta clase por cada fila; en JDBC lo hacías a mano con rs.getInt(...), rs.getString(...).
 * Cada atributo se corresponde con una columna. En CineFest no hay relaciones entre entidades (@ManyToOne, @OneToMany):
 * las claves foráneas se guardan como simples Integer. La sección 3 de la guía muestra cómo se escribiría una relación.
 */
// @Entity: marca la clase como entidad, para que Hibernate la gestione (también debe estar en persistence.xml)
@Entity
// @Table: nombre de la tabla de SQLite a la que se asocia esta clase
@Table(name = "espectadores")
public class EspectadorEntity {
    // @Id: este atributo es la clave primaria (PRIMARY KEY) de la tabla
    @Id
    // @GeneratedValue IDENTITY: el id lo genera SQLite (AUTOINCREMENT); tras el persist, Hibernate lo rellena solo, como getGeneratedKeys en JDBC
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column: columna id_espectador; es una clave foránea guardada como Integer simple (sin @ManyToOne, no hay relaciones en CineFest)
    @Column(name = "id_espectador")
    private Integer idEspectador;
    // @Column: enlaza el atributo con la columna nombre
    @Column(name = "nombre")
    private String nombre;
    // @Column: enlaza el atributo con la columna email
    @Column(name = "email")
    private String email;
    // @Column: enlaza el atributo con la columna fecha_alta (se pone el nombre porque en Java usamos camelCase y en SQL snake_case)
    @Column(name = "fecha_alta")
    private String fechaAlta;
    // @Column: enlaza el atributo con la columna activo
    @Column(name = "activo")
    private Integer activo;
    /**
     * Constructor vacío: JPA lo exige para crear los objetos al leer filas. No se debe borrar ni hacerlo privado.
     */
    public EspectadorEntity() {}
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
     * Devuelve email.
     *
     * @return valor actual de email
     */
    public String getEmail() { return email; }
    /**
     * Cambia email.
     *
     * @param value nuevo valor de email
     */
    public void setEmail(String value) { this.email = value; }
    /**
     * Devuelve fecha alta.
     *
     * @return valor actual de fecha alta
     */
    public String getFechaAlta() { return fechaAlta; }
    /**
     * Cambia fecha alta.
     *
     * @param value nuevo valor de fecha alta
     */
    public void setFechaAlta(String value) { this.fechaAlta = value; }
    /**
     * Devuelve activo.
     *
     * @return valor actual de activo
     */
    public Integer getActivo() { return activo; }
    /**
     * Cambia activo.
     *
     * @param value nuevo valor de activo
     */
    public void setActivo(Integer value) { this.activo = value; }
}
