package es.ejemplo.academia.infra;

import es.ejemplo.academia.dao.DaoException;
import org.sqlite.SQLiteConfig;

import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/**
 * Padre de los DAO: guarda la url, abre una conexión nueva en cada operación y hace el JDBC.
 * No conoce las columnas de ninguna tabla: cada hijo convierte el ResultSet con map.
 *
 * @param <T> entidad que maneja el DAO hijo
 */
public abstract class AbstractDao<T> {

    private final String url;

    /**
     * Guarda la url. No comprueba nada: las comprobaciones se hacen al abrir cada conexión.
     *
     * @param url url JDBC de SQLite, por ejemplo jdbc:sqlite:datos/academia.sqlite3
     */
    protected AbstractDao(String url) {
        this.url = url;
    }

    /**
     * Convierte las filas del ResultSet en entidades. Cada hijo lo implementa con el while
     * y leyendo las columnas por nombre.
     *
     * @param rs resultado de la consulta, sin recorrer
     * @return lista de entidades, vacía si no hay filas
     */
    protected abstract List<T> map(ResultSet rs);

    /**
     * Comprueba la url y que el fichero de la base de datos existe, abre la conexión
     * y activa las claves foráneas.
     */
    protected Connection obtenerConexion() {
        if (url == null || !url.startsWith("jdbc:sqlite:")) {
            throw new IllegalArgumentException("URL Sqlite no valida");
        }

        String ruta = url.replace("jdbc:sqlite:", "");

        Path path = Path.of(ruta);

        if (Files.notExists(path) || !Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Ruta no existe");
        }

        try {
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA foreign_keys = ON");

            return connection;

        } catch (SQLException e) {
            throw new DaoException("No fue posible establecer la conexion", e);
        }
    }


    /**
     * Ejecuta un SELECT con sus ? y devuelve las filas convertidas con map.
     */
    protected List<T> findAll(String sql, Object... valores) {
        throw new UnsupportedOperationException("Sin implementar");
    }

    /**
     * Ejecuta un UPDATE o un DELETE y devuelve cuántas filas ha cambiado.
     */
    protected int ejecutar(String sql, Object... valores) {
        throw new UnsupportedOperationException("Sin implementar");
    }

    /**
     * Ejecuta un INSERT y devuelve el id que genera SQLite.
     */
    protected int insertarYDevolverId(String sql, Object... valores) {
        throw new UnsupportedOperationException("Sin implementar");
    }

    /**
     * Ejecuta un SELECT que devuelve un solo número (una suma). Sin resultado, cero.
     */
    protected BigDecimal consultarDecimal(String sql, Object... valores) {
        throw new UnsupportedOperationException("Sin implementar");
    }
}
