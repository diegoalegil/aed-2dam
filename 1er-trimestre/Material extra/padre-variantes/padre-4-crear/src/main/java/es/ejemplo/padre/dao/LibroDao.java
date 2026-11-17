package es.ejemplo.padre.dao;

import es.ejemplo.padre.infra.AbstractDao;
import es.ejemplo.padre.modelo.Libro;

import java.sql.*;
import java.util.*;

/**
 * DAO de la tabla libros. La conexión la da el padre AbstractDao.
 */
public final class LibroDao extends AbstractDao<Libro> {

    /**
     * Crea el DAO pasando la url al padre.
     *
     * @param url url JDBC de SQLite, por ejemplo jdbc:sqlite:datos/libros.sqlite3
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
     * Devuelve los libros ordenados por id.
     *
     * @return lista de libros, vacía si no hay
     */
    public List<Libro> listar() {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, titulo, id_autor FROM libros ORDER BY id");
            ResultSet rs = preparedStatement.executeQuery();

            return map(rs);

        } catch (SQLException e) {
            throw new DaoException("Error obteniendo libros", e);
        }
    }

    /**
     * Inserta un libro y devuelve el id que le asigna SQLite.
     *
     * @param l libro a insertar (su id se ignora)
     * @return id del libro nuevo, 0 si no se pudo leer
     */
    public int insertar(Libro l) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO libros (titulo, id_autor) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, l.getTitulo());
            preparedStatement.setInt(2, l.getIdAutor());

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DaoException("Error insertando libro", e);
        }

        return 0;
    }
}
