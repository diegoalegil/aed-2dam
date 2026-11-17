package es.ejemplo.padre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Crea en cada test una base de datos nueva a partir de schema.sql, con el autor 1 'Ana'.
 */
abstract class BaseTest {

    @TempDir
    Path carpeta;

    /** Fichero de la base de datos del test. */
    Path ruta;

    /** Url JDBC del fichero. */
    String url;

    @BeforeEach
    void prepararBase() throws Exception {
        ruta = carpeta.resolve("biblioteca.sqlite3");
        url = "jdbc:sqlite:" + ruta.toAbsolutePath();

        String schema;
        try (InputStream in = getClass().getResourceAsStream("/schema.sql")) {
            schema = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            for (String sql : schema.split(";")) {
                if (!sql.isBlank()) {
                    statement.execute(sql);
                }
            }
            statement.execute("INSERT INTO autores (id, nombre) VALUES (1, 'Ana')");
        }
    }
}
