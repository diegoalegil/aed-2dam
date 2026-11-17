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
 * Crea antes de cada prueba una base de datos nueva a partir de schema.sql,
 * con el autor 1 'Ana' ya insertado.
 */
abstract class BaseTest {

    @TempDir
    Path carpeta;

    /** Fichero .sqlite3 de la prueba. */
    protected Path path;

    /** Url JDBC del fichero de la prueba. */
    protected String url;

    @BeforeEach
    void crearBase() throws Exception {
        path = carpeta.resolve("biblioteca.sqlite3");
        url = "jdbc:sqlite:" + path.toAbsolutePath();

        String schema;
        try (InputStream in = BaseTest.class.getResourceAsStream("/schema.sql")) {
            schema = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            for (String sentencia : schema.split(";")) {
                if (!sentencia.isBlank()) {
                    statement.execute(sentencia);
                }
            }
            statement.execute("INSERT INTO autores (id, nombre) VALUES (1, 'Ana')");
        }
    }
}
