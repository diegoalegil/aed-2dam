package es.ejemplo.cinefest.modelo;

/**
 * Modelo Sala: una sala de una sede (una fila de la tabla salas).
 * Es un POJO: una clase simple con atributos privados, constructor vacío y getters y setters, sin lógica ni anotaciones JPA.
 * Es lo que devuelven los DAO; las entidades JPA (orm/entity) no salen del DAO, el mapper convierte una en otra.
 * Los atributos son Integer y Double (envoltorios) y no int y double porque pueden valer null
 * (por ejemplo, un id que todavía no existe antes de insertar); un int nunca puede ser null.
 */
public class Sala {
    // Atributos: uno por columna de la tabla, con el mismo nombre en camelCase (id_sede se llama idSede)
    private Integer idSala;
    private String nombre;
    private Integer aforo;
    private Integer idSede;

    /**
     * Constructor vacío: crea el objeto sin datos; los atributos se rellenan después con los setters.
     */
    public Sala() {}

    // Getters y setters: leer y cambiar cada atributo; los mappers los usan para copiar datos entre entidad y modelo
    /** @return id de la sala (clave primaria, columna id_sala) */
    public Integer getIdSala() { return idSala; }
    /** @param value id de la sala (clave primaria, columna id_sala) */
    public void setIdSala(Integer value) { this.idSala = value; }
    /** @return nombre de la sala (columna nombre, único dentro de su sede) */
    public String getNombre() { return nombre; }
    /** @param value nombre de la sala (columna nombre, único dentro de su sede) */
    public void setNombre(String value) { this.nombre = value; }
    /** @return número de plazas de la sala (columna aforo, mayor que 0) */
    public Integer getAforo() { return aforo; }
    /** @param value número de plazas de la sala (columna aforo, mayor que 0) */
    public void setAforo(Integer value) { this.aforo = value; }
    /** @return id de la sede a la que pertenece (clave foránea, columna id_sede) */
    public Integer getIdSede() { return idSede; }
    /** @param value id de la sede a la que pertenece (clave foránea, columna id_sede) */
    public void setIdSede(Integer value) { this.idSede = value; }
}
