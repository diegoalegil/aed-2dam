package es.ejemplo.cinefest.modelo;

/**
 * Modelo Valoracion: la puntuación y el comentario de un espectador sobre una película (una fila de la tabla valoraciones).
 * Es un POJO: una clase simple con atributos privados, constructor vacío y getters y setters, sin lógica ni anotaciones JPA.
 * Es lo que devuelven los DAO; las entidades JPA (orm/entity) no salen del DAO, el mapper convierte una en otra.
 * Los atributos son Integer y Double (envoltorios) y no int y double porque pueden valer null
 * (por ejemplo, un id que todavía no existe antes de insertar); un int nunca puede ser null.
 */
public class Valoracion {
    // Atributos: uno por columna de la tabla, con el mismo nombre en camelCase (id_sede se llama idSede)
    private Integer idValoracion;
    private Integer idPelicula;
    private Integer idEspectador;
    private Integer puntuacion;
    private String comentario;
    private String fecha;

    /**
     * Constructor vacío: crea el objeto sin datos; los atributos se rellenan después con los setters.
     */
    public Valoracion() {}

    // Getters y setters: leer y cambiar cada atributo; los mappers los usan para copiar datos entre entidad y modelo
    /** @return id de la valoración (clave primaria, columna id_valoracion) */
    public Integer getIdValoracion() { return idValoracion; }
    /** @param value id de la valoración (clave primaria, columna id_valoracion) */
    public void setIdValoracion(Integer value) { this.idValoracion = value; }
    /** @return id de la película valorada (clave foránea, columna id_pelicula) */
    public Integer getIdPelicula() { return idPelicula; }
    /** @param value id de la película valorada (clave foránea, columna id_pelicula) */
    public void setIdPelicula(Integer value) { this.idPelicula = value; }
    /** @return id del espectador que valora (clave foránea, columna id_espectador) */
    public Integer getIdEspectador() { return idEspectador; }
    /** @param value id del espectador que valora (clave foránea, columna id_espectador) */
    public void setIdEspectador(Integer value) { this.idEspectador = value; }
    /** @return puntuación de 1 a 5 (columna puntuacion) */
    public Integer getPuntuacion() { return puntuacion; }
    /** @param value puntuación de 1 a 5 (columna puntuacion) */
    public void setPuntuacion(Integer value) { this.puntuacion = value; }
    /** @return comentario opcional (columna comentario, puede ser null) */
    public String getComentario() { return comentario; }
    /** @param value comentario opcional (columna comentario, puede ser null) */
    public void setComentario(String value) { this.comentario = value; }
    /** @return fecha de la valoración en texto (columna fecha) */
    public String getFecha() { return fecha; }
    /** @param value fecha de la valoración en texto (columna fecha) */
    public void setFecha(String value) { this.fecha = value; }
}
