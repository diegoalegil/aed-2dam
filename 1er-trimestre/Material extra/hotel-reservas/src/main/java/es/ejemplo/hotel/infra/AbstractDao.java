package es.ejemplo.hotel.infra;

import es.ejemplo.hotel.dao.DaoException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: padre de los DAO.
 */
public abstract class AbstractDao<T> {

    protected final String url;


    protected AbstractDao(String url) {

        if (url == null || !url.startsWith("jdbc:sqlite:")) {
            throw new IllegalArgumentException("Url invalida");
        }
        String ruta = url.replace("jdbc:sqlite:", "");
        if (!Files.isRegularFile(Path.of(ruta))) {
            throw new DaoException("Error", null);
        }
        try (Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM sqlite_master WHERE type = 'table'");
            List<String> tablas = new ArrayList<>();
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                tablas.add(rs.getString("name"));
            }

            if (!tablas.containsAll(List.of("huespedes", "habitaciones", "reservas", "consumos"))) {
                throw new DaoException("Error", null);
            }

        } catch (SQLException e) {
            throw new DaoException("Error", e);
        }

        this.url = url;
    }

    protected Connection obtenerConexion() {
        try {
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.execute("PRAGMA foreign_keys  = ON");
            return connection;
        } catch (SQLException e) {
            throw new DaoException("Error", e);
        }
    }

    protected abstract List<T> map(ResultSet rs);
}
