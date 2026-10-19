package es.ejemplo.anime.config;

import es.ejemplo.anime.exception.AplicationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Abre conexiones con la base SQLite indicada por la url y prepara la tabla.
 */
public class ConexionBD {
    private final String url;

    public ConexionBD(String url) {
        this.url = url;
    }

    /**
     * Devuelve una conexión abierta con la base de la url.
     * Si la carpeta donde va el fichero de la base no existe, la crea antes de conectar.
     */
    public Connection obtenerConexion() {

        String ruta = url.replace("jdbc:sqlite:", "");

        Path path = Path.of(ruta);

        Path carpeta = path.getParent();

        if (Files.notExists(carpeta)) {
            try {
                Files.createDirectories(carpeta);
            } catch (IOException e) {
                throw new AplicationException("Error creando la ruta", e);
            }
        }
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new AplicationException("Error conectando con la base", e);
        }
    }

    /**
     * Crea la tabla series si todavía no existe (la sentencia está en sql/schema.sql).
     * Se puede llamar varias veces sin perder los datos guardados.
     */
    public void inicializar() {
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS series (\n" +
                    "    codigo    TEXT    PRIMARY KEY NOT NULL,\n" +
                    "    titulo    TEXT    NOT NULL,\n" +
                    "    genero    TEXT    NOT NULL,\n" +
                    "    episodios INTEGER NOT NULL,\n" +
                    "    nota      REAL    NOT NULL\n" +
                    ");\n");
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new AplicationException("Error inicializando la conexion", e);
        }
    }
}
