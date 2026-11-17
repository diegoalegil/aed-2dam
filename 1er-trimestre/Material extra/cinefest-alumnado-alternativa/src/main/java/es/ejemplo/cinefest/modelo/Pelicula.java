package es.ejemplo.cinefest.modelo;

/**
 * Modelo Pelicula: una película del festival (una fila de la tabla peliculas).
 * Es un POJO: una clase simple con atributos privados, constructor vacío y getters y setters, sin lógica ni anotaciones JPA.
 * Es lo que devuelven los DAO; las entidades JPA (orm/entity) no salen del DAO, el mapper convierte una en otra.
 * Los atributos son Integer y Double (envoltorios) y no int y double porque pueden valer null
 * (por ejemplo, un id que todavía no existe antes de insertar); un int nunca puede ser null.
 */
public class Pelicula {
    // Atributos: uno por columna de la tabla, con el mismo nombre en camelCase (id_sede se llama idSede)
    private Integer idPelicula;
    private String titulo;
    private String genero;
    private Integer duracionMin;
    private String pais;
    private Integer anio;

    /**
     * Constructor vacío: crea el objeto sin datos; los atributos se rellenan después con los setters.
     */
    public Pelicula() {}

    // Getters y setters: leer y cambiar cada atributo; los mappers los usan para copiar datos entre entidad y modelo
    /** @return id de la película (clave primaria, columna id_pelicula) */
    public Integer getIdPelicula() { return idPelicula; }
    /** @param value id de la película (clave primaria, columna id_pelicula) */
    public void setIdPelicula(Integer value) { this.idPelicula = value; }
    /** @return título de la película (columna titulo) */
    public String getTitulo() { return titulo; }
    /** @param value título de la película (columna titulo) */
    public void setTitulo(String value) { this.titulo = value; }
    /** @return género de la película (columna genero) */
    public String getGenero() { return genero; }
    /** @param value género de la película (columna genero) */
    public void setGenero(String value) { this.genero = value; }
    /** @return duración en minutos (columna duracion_min, mayor que 0) */
    public Integer getDuracionMin() { return duracionMin; }
    /** @param value duración en minutos (columna duracion_min, mayor que 0) */
    public void setDuracionMin(Integer value) { this.duracionMin = value; }
    /** @return país de la película (columna pais) */
    public String getPais() { return pais; }
    /** @param value país de la película (columna pais) */
    public void setPais(String value) { this.pais = value; }
    /** @return año de estreno (columna anio) */
    public Integer getAnio() { return anio; }
    /** @param value año de estreno (columna anio) */
    public void setAnio(Integer value) { this.anio = value; }
}
