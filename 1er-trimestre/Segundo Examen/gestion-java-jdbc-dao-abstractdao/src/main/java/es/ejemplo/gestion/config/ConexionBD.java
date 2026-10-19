package es.ejemplo.gestion.config;

import es.ejemplo.gestion.exception.AplicationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abre una conexión en la base SQLite indicada por el constructor.
 */
public class ConexionBD {
    private String url;

    public ConexionBD(String url) {
        this.url = url;
    }

    public Connection obtenerConexion() {

        String ruta =url.replace("jdbc:sqlite:", "");

       Path path = Path.of(ruta);

       if (Files.notExists(path)){
           throw new AplicationException("Error encontrando la ruta");
       }

        try {

            return DriverManager.getConnection(url);

        } catch (SQLException e) {
            throw new AplicationException("Error conectandose a la base de datos", e);
        }
    }
}
