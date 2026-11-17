package es.ejemplo.padre.modelo;

/**
 * Libro de la tabla libros. Cada libro pertenece a un autor (id_autor).
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
     * @param id      id del libro, null si aún no está en la base de datos
     * @param titulo  título del libro
     * @param idAutor id del autor
     */
    public Libro(Integer id, String titulo, Integer idAutor) {
        this.id = id;
        this.titulo = titulo;
        this.idAutor = idAutor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(Integer idAutor) {
        this.idAutor = idAutor;
    }

    @Override
    public String toString() {
        return "Libro{id=" + id + ", titulo='" + titulo + "', idAutor=" + idAutor + "}";
    }
}
