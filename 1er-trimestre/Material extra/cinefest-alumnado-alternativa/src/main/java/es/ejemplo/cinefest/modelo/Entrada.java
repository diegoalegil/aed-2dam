package es.ejemplo.cinefest.modelo;

/**
 * Modelo Entrada: una entrada comprada por un espectador para una sesión (una fila de la tabla entradas).
 * Es un POJO: una clase simple con atributos privados, constructor vacío y getters y setters, sin lógica ni anotaciones JPA.
 * Es lo que devuelven los DAO; las entidades JPA (orm/entity) no salen del DAO, el mapper convierte una en otra.
 * Los atributos son Integer y Double (envoltorios) y no int y double porque pueden valer null
 * (por ejemplo, un id que todavía no existe antes de insertar); un int nunca puede ser null.
 */
public class Entrada {
    // Atributos: uno por columna de la tabla, con el mismo nombre en camelCase (id_sede se llama idSede)
    private Integer idEntrada;
    private Integer idSesion;
    private Integer idEspectador;
    private String fechaCompra;
    private String estado;

    /**
     * Constructor vacío: crea el objeto sin datos; los atributos se rellenan después con los setters.
     */
    public Entrada() {}

    // Getters y setters: leer y cambiar cada atributo; los mappers los usan para copiar datos entre entidad y modelo
    /** @return id de la entrada (clave primaria, columna id_entrada) */
    public Integer getIdEntrada() { return idEntrada; }
    /** @param value id de la entrada (clave primaria, columna id_entrada) */
    public void setIdEntrada(Integer value) { this.idEntrada = value; }
    /** @return id de la sesión (clave foránea, columna id_sesion) */
    public Integer getIdSesion() { return idSesion; }
    /** @param value id de la sesión (clave foránea, columna id_sesion) */
    public void setIdSesion(Integer value) { this.idSesion = value; }
    /** @return id del espectador que la compró (clave foránea, columna id_espectador) */
    public Integer getIdEspectador() { return idEspectador; }
    /** @param value id del espectador que la compró (clave foránea, columna id_espectador) */
    public void setIdEspectador(Integer value) { this.idEspectador = value; }
    /** @return fecha de compra en texto (columna fecha_compra) */
    public String getFechaCompra() { return fechaCompra; }
    /** @param value fecha de compra en texto (columna fecha_compra) */
    public void setFechaCompra(String value) { this.fechaCompra = value; }
    /** @return estado: PAGADA, USADA o CANCELADA (columna estado) */
    public String getEstado() { return estado; }
    /** @param value estado: PAGADA, USADA o CANCELADA (columna estado) */
    public void setEstado(String value) { this.estado = value; }
}
