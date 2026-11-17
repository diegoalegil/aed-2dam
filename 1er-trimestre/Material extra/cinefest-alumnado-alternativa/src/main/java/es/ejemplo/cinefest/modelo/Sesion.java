package es.ejemplo.cinefest.modelo;

/**
 * Modelo Sesion: una sesión: una película proyectada en una sala a una fecha y hora (una fila de la tabla sesiones).
 * Es un POJO: una clase simple con atributos privados, constructor vacío y getters y setters, sin lógica ni anotaciones JPA.
 * Es lo que devuelven los DAO; las entidades JPA (orm/entity) no salen del DAO, el mapper convierte una en otra.
 * Los atributos son Integer y Double (envoltorios) y no int y double porque pueden valer null
 * (por ejemplo, un id que todavía no existe antes de insertar); un int nunca puede ser null.
 */
public class Sesion {
    // Atributos: uno por columna de la tabla, con el mismo nombre en camelCase (id_sede se llama idSede)
    private Integer idSesion;
    private Integer idPelicula;
    private Integer idSala;
    private String fecha;
    private String hora;
    private Double precio;
    private String estado;

    /**
     * Constructor vacío: crea el objeto sin datos; los atributos se rellenan después con los setters.
     */
    public Sesion() {}

    // Getters y setters: leer y cambiar cada atributo; los mappers los usan para copiar datos entre entidad y modelo
    /** @return id de la sesión (clave primaria, columna id_sesion) */
    public Integer getIdSesion() { return idSesion; }
    /** @param value id de la sesión (clave primaria, columna id_sesion) */
    public void setIdSesion(Integer value) { this.idSesion = value; }
    /** @return id de la película que se proyecta (clave foránea, columna id_pelicula) */
    public Integer getIdPelicula() { return idPelicula; }
    /** @param value id de la película que se proyecta (clave foránea, columna id_pelicula) */
    public void setIdPelicula(Integer value) { this.idPelicula = value; }
    /** @return id de la sala donde se proyecta (clave foránea, columna id_sala) */
    public Integer getIdSala() { return idSala; }
    /** @param value id de la sala donde se proyecta (clave foránea, columna id_sala) */
    public void setIdSala(Integer value) { this.idSala = value; }
    /** @return fecha de la sesión en texto, por ejemplo "2026-11-13" (columna fecha) */
    public String getFecha() { return fecha; }
    /** @param value fecha de la sesión en texto, por ejemplo "2026-11-13" (columna fecha) */
    public void setFecha(String value) { this.fecha = value; }
    /** @return hora de la sesión en texto, por ejemplo "20:30" (columna hora) */
    public String getHora() { return hora; }
    /** @param value hora de la sesión en texto, por ejemplo "20:30" (columna hora) */
    public void setHora(String value) { this.hora = value; }
    /** @return precio de la entrada (columna precio, 0 o más) */
    public Double getPrecio() { return precio; }
    /** @param value precio de la entrada (columna precio, 0 o más) */
    public void setPrecio(Double value) { this.precio = value; }
    /** @return estado: PROGRAMADA, CANCELADA o FINALIZADA (columna estado) */
    public String getEstado() { return estado; }
    /** @param value estado: PROGRAMADA, CANCELADA o FINALIZADA (columna estado) */
    public void setEstado(String value) { this.estado = value; }
}
