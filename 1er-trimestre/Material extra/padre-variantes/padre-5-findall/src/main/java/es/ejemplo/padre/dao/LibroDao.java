package es.ejemplo.padre.dao;

import es.ejemplo.padre.infra.AbstractDao;
import es.ejemplo.padre.modelo.Libro;

import java.sql.*;
import java.util.*;

/**
 * DAO de la tabla libros. No abre conexiones: le pasa la SQL y los valores al padre.
 */
public final class LibroDao extends AbstractDao<Libro> {

    /**
     * Crea el DAO.
     *
     * @param url url JDBC de SQLite
     */
    public LibroDao(String url) {
        super(url);
    }

    @Override
    protected List<Libro> map(ResultSet rs) {
        List<Libro> lista = new ArrayList<>();
        try {

            while (rs.next()) {
                lista.add(new Libro(rs.getInt("id"), rs.getString("titulo"), rs.getInt("id_autor")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error haciendo map", e);
        }
        return lista;
    }

    /**
     * Lista los libros ordenados por id.
     *
     * @return lista de libros, vacía si no hay
     */
    public List<Libro> listar() {
        return findAll("SELECT id, titulo, id_autor FROM libros ORDER BY id");
    }

    /**
     * Busca los libros cuyo título contiene el texto, ordenados por título y id.
     *
     * @param texto parte del título
     * @return lista de libros, vacía si el texto es null o está en blanco
     */
    public List<Libro> buscarPorTitulo(String texto) {

        if (texto == null || texto.isBlank()) {
            return new ArrayList<>();
        }

        return findAll("SELECT id, titulo, id_autor FROM libros WHERE titulo LIKE ? ORDER BY titulo, id", "%" + texto + "%");
    }

    /**
     * Inserta un libro.
     *
     * @param l libro a guardar
     * @return id nuevo que crea SQLite
     */
    public int insertar(Libro l) {
        return insertarYDevolverId("INSERT INTO libros (titulo, id_autor) VALUES (?, ?)", l.getTitulo(), l.getIdAutor());
    }

    /**
     * Elimina un libro por id.
     *
     * @param id id del libro
     * @return 1 si lo ha borrado, 0 si no existía
     */
    public int eliminar(Integer id) {
        return ejecutar("DELETE FROM libros WHERE id = ?", id);
    }
}
