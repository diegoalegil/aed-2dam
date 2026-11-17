package es.ejemplo.padre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Crea para cada test una base SQLite nueva en una carpeta temporal a partir de schema.sql,
 * con el autor 1 'Ana'.
 */
public abstract class BaseTest {

    @TempDir
    protected Path carpeta;

    protected Path path;
    protected String url;

    @BeforeEach
    void crearBase() throws Exception {
        path = carpeta.resolve("biblioteca.sqlite3");
        url = urlDe(path);
        String schema;
        try (InputStream in = BaseTest.class.getResourceAsStream("/schema.sql")) {
            schema = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        ejecutar(url, schema.split(";"));
        ejecutar(url, "INSERT INTO autores (id, nombre) VALUES (1, 'Ana')");
    }

    /** Url JDBC de un fichero. */
    protected static String urlDe(Path fichero) {
        return "jdbc:sqlite:" + fichero.toAbsolutePath();
    }

    /** Ejecuta sentencias sueltas con JDBC directo (si el fichero no existe, SQLite lo crea). */
    protected static void ejecutar(String url, String... sentencias) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            for (String sql : sentencias) {
                if (!sql.isBlank()) {
                    statement.execute(sql);
                }
            }
        }
    }
}
