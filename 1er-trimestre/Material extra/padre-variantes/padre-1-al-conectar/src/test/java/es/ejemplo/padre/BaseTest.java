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
 * Crea una base de datos nueva para cada test a partir de schema.sql.
 */
abstract class BaseTest {

    @TempDir
    Path carpeta;

    /** Fichero de la base de datos del test. */
    protected Path path;

    /** Url JDBC del fichero. */
    protected String url;

    @BeforeEach
    void crearBase() throws Exception {
        path = carpeta.resolve("libros.sqlite3");
        url = "jdbc:sqlite:" + path.toAbsolutePath();

        String sql;
        try (InputStream in = BaseTest.class.getResourceAsStream("/schema.sql")) {
            sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            for (String sentencia : sql.split(";")) {
                if (!sentencia.isBlank()) {
                    statement.execute(sentencia);
                }
            }
            statement.execute("INSERT INTO autores (id, nombre) VALUES (1, 'Ana')");
        }
    }
}
