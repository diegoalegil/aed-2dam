package es.ejemplo.cinefest.modelo;

/**
 * Modelo Sede: un cine o sede del festival (una fila de la tabla sedes).
 * Es un POJO: una clase simple con atributos privados, constructor vacío y getters y setters, sin lógica ni anotaciones JPA.
 * Es lo que devuelven los DAO; las entidades JPA (orm/entity) no salen del DAO, el mapper convierte una en otra.
 * Los atributos son Integer y Double (envoltorios) y no int y double porque pueden valer null
 * (por ejemplo, un id que todavía no existe antes de insertar); un int nunca puede ser null.
 */
public class Sede {
    // Atributos: uno por columna de la tabla, con el mismo nombre en camelCase (id_sede se llama idSede)
    private Integer idSede;
    private String nombre;
    private String municipio;

    /**
     * Constructor vacío: crea el objeto sin datos; los atributos se rellenan después con los setters.
     */
    public Sede() {}

    // Getters y setters: leer y cambiar cada atributo; los mappers los usan para copiar datos entre entidad y modelo
    /** @return id de la sede (clave primaria, columna id_sede) */
    public Integer getIdSede() { return idSede; }
    /** @param value id de la sede (clave primaria, columna id_sede) */
    public void setIdSede(Integer value) { this.idSede = value; }
    /** @return nombre de la sede (columna nombre, único) */
    public String getNombre() { return nombre; }
    /** @param value nombre de la sede (columna nombre, único) */
    public void setNombre(String value) { this.nombre = value; }
    /** @return municipio donde está la sede (columna municipio) */
    public String getMunicipio() { return municipio; }
    /** @param value municipio donde está la sede (columna municipio) */
    public void setMunicipio(String value) { this.municipio = value; }
}
