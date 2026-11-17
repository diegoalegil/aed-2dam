package es.ejemplo.cinefest.modelo;

/**
 * Modelo Espectador: una persona que puede comprar entradas y valorar películas (una fila de la tabla espectadores).
 * Es un POJO: una clase simple con atributos privados, constructor vacío y getters y setters, sin lógica ni anotaciones JPA.
 * Es lo que devuelven los DAO; las entidades JPA (orm/entity) no salen del DAO, el mapper convierte una en otra.
 * Los atributos son Integer y Double (envoltorios) y no int y double porque pueden valer null
 * (por ejemplo, un id que todavía no existe antes de insertar); un int nunca puede ser null.
 */
public class Espectador {
    // Atributos: uno por columna de la tabla, con el mismo nombre en camelCase (id_sede se llama idSede)
    private Integer idEspectador;
    private String nombre;
    private String email;
    private String fechaAlta;
    private Integer activo;

    /**
     * Constructor vacío: crea el objeto sin datos; los atributos se rellenan después con los setters.
     */
    public Espectador() {}

    // Getters y setters: leer y cambiar cada atributo; los mappers los usan para copiar datos entre entidad y modelo
    /** @return id del espectador (clave primaria, columna id_espectador) */
    public Integer getIdEspectador() { return idEspectador; }
    /** @param value id del espectador (clave primaria, columna id_espectador) */
    public void setIdEspectador(Integer value) { this.idEspectador = value; }
    /** @return nombre del espectador (columna nombre) */
    public String getNombre() { return nombre; }
    /** @param value nombre del espectador (columna nombre) */
    public void setNombre(String value) { this.nombre = value; }
    /** @return correo electrónico (columna email, único) */
    public String getEmail() { return email; }
    /** @param value correo electrónico (columna email, único) */
    public void setEmail(String value) { this.email = value; }
    /** @return fecha de alta en texto (columna fecha_alta) */
    public String getFechaAlta() { return fechaAlta; }
    /** @param value fecha de alta en texto (columna fecha_alta) */
    public void setFechaAlta(String value) { this.fechaAlta = value; }
    /** @return 1 si está activo, 0 si no (columna activo; es un Integer porque SQLite no tiene boolean) */
    public Integer getActivo() { return activo; }
    /** @param value 1 si está activo, 0 si no (columna activo; es un Integer porque SQLite no tiene boolean) */
    public void setActivo(Integer value) { this.activo = value; }
}
