package es.ejemplo.padre.modelo;

/**
 * Libro de la tabla libros.
 */
public class Libro {

    private Integer id;
    private String titulo;
    private Integer idAutor;

    /**
     * Crea un libro vacío.
     */
    public Libro() {
    }

    /**
     * Crea un libro con sus datos.
     *
     * @param id      id del libro (null si es nuevo)
     * @param titulo  título del libro
     * @param idAutor id del autor
     */
    public Libro(Integer id, String titulo, Integer idAutor) {
        this.id = id;
        this.titulo = titulo;
        this.idAutor = idAutor;
    }

    /**
     * @return id del libro
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id nuevo id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return título del libro
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo nuevo título
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return id del autor
     */
    public Integer getIdAutor() {
        return idAutor;
    }

    /**
     * @param idAutor nuevo id del autor
     */
    public void setIdAutor(Integer idAutor) {
        this.idAutor = idAutor;
    }

    /**
     * @return texto con los datos del libro
     */
    @Override
    public String toString() {
        return "Libro{id=" + id + ", titulo='" + titulo + "', idAutor=" + idAutor + "}";
    }
}
