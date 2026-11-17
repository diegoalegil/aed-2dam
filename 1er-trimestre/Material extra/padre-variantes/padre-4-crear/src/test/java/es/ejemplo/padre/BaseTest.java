package es.ejemplo.padre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Base de los tests: crea en una carpeta temporal una base de datos nueva a partir de
 * schema.sql (con JDBC directo, sin los DAO) e inserta el autor 1 'Ana'.
 */
public abstract class BaseTest {

    /** Carpeta temporal, nueva en cada test. */
    @TempDir
    protected Path carpeta;

    /** Fichero de la base de datos de prueba. */
    protected Path ruta;

    /** Url JDBC de la base de datos de prueba. */
    protected String url;

    /** Crea la base de datos de prueba antes de cada test. */
    @BeforeEach
    void crearBaseDeDatos() throws Exception {
        ruta = carpeta.resolve("libros.sqlite3");
        url = "jdbc:sqlite:" + ruta.toAbsolutePath();

        String schema;
        try (InputStream in = BaseTest.class.getResourceAsStream("/schema.sql")) {
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

    /** Ejecuta una sentencia con JDBC directo, sin pasar por los DAO. */
    protected void ejecutarSql(String urlBd, String sql) throws Exception {
        try (Connection connection = DriverManager.getConnection(urlBd);
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /** Ejecuta con JDBC directo una consulta que devuelve un número. */
    protected int consultarNumero(String urlBd, String sql) throws Exception {
        try (Connection connection = DriverManager.getConnection(urlBd);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
