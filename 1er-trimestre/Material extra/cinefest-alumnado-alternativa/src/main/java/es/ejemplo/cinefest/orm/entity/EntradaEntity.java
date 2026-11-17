package es.ejemplo.cinefest.orm.entity;

// Importa todas las anotaciones de JPA (@Entity, @Table, @Id, @Column...) del paquete jakarta.persistence
import jakarta.persistence.*;

/**
 * Entidad JPA: clase que representa una fila de la tabla entradas, es decir, una entrada comprada para una sesión por un espectador.
 * Hibernate crea un objeto de esta clase por cada fila; en JDBC lo hacías a mano con rs.getInt(...), rs.getString(...).
 * Cada atributo se corresponde con una columna. En CineFest no hay relaciones entre entidades (@ManyToOne, @OneToMany):
 * las claves foráneas se guardan como simples Integer. La sección 3 de la guía muestra cómo se escribiría una relación.
 */
// @Entity: marca la clase como entidad, para que Hibernate la gestione (también debe estar en persistence.xml)
@Entity
// @Table: nombre de la tabla de SQLite a la que se asocia esta clase
@Table(name = "entradas")
public class EntradaEntity {
    // @Id: este atributo es la clave primaria (PRIMARY KEY) de la tabla
    @Id
    // @GeneratedValue IDENTITY: el id lo genera SQLite (AUTOINCREMENT); tras el persist, Hibernate lo rellena solo, como getGeneratedKeys en JDBC
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column: enlaza el atributo con la columna id_entrada
    @Column(name = "id_entrada")
    private Integer idEntrada;
    // @Column: columna id_sesion; es una clave foránea guardada como Integer simple (sin @ManyToOne, no hay relaciones en CineFest)
    @Column(name = "id_sesion")
    private Integer idSesion;
    // @Column: columna id_espectador; es una clave foránea guardada como Integer simple (sin @ManyToOne, no hay relaciones en CineFest)
    @Column(name = "id_espectador")
    private Integer idEspectador;
    // @Column: enlaza el atributo con la columna fecha_compra (se pone el nombre porque en Java usamos camelCase y en SQL snake_case)
    @Column(name = "fecha_compra")
    private String fechaCompra;
    // @Column: enlaza el atributo con la columna estado
    @Column(name = "estado")
    private String estado;

    /**
     * Constructor vacío: JPA lo exige para crear los objetos al leer filas. No se debe borrar ni hacerlo privado.
     */
    public EntradaEntity() {
    }

    /**
     * Devuelve id entrada.
     *
     * @return valor actual de id entrada
     */
    public Integer getIdEntrada() {
        return idEntrada;
    }

    /**
     * Cambia id entrada.
     *
     * @param value nuevo valor de id entrada
     */
    public void setIdEntrada(Integer value) {
        this.idEntrada = value;
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
     * Devuelve id espectador.
     *
     * @return valor actual de id espectador
     */
    public Integer getIdEspectador() {
        return idEspectador;
    }

    /**
     * Cambia id espectador.
     *
     * @param value nuevo valor de id espectador
     */
    public void setIdEspectador(Integer value) {
        this.idEspectador = value;
    }

    /**
     * Devuelve fecha compra.
     *
     * @return valor actual de fecha compra
     */
    public String getFechaCompra() {
        return fechaCompra;
    }

    /**
     * Cambia fecha compra.
     *
     * @param value nuevo valor de fecha compra
     */
    public void setFechaCompra(String value) {
        this.fechaCompra = value;
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
