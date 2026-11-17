package es.ejemplo.arcade;

import es.ejemplo.arcade.config.ConexionBD;
import es.ejemplo.arcade.exception.AplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de ConexionBD. No puntúan, pero sin conexión no funciona ningún DAO.
 */
class ConexionBDTest {
    @TempDir
    Path carpeta;

    private String url() {
        return "jdbc:sqlite:" + carpeta.resolve("datos").resolve("arcade.sqlite3").toAbsolutePath();
    }

    private int contar(String sql) throws Exception {
        try (Connection con = DriverManager.getConnection(url());
             ResultSet rs = con.createStatement().executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    @Test
    void conectaSiLaCarpetaYaExiste() throws Exception {
        Files.createDirectories(carpeta.resolve("datos"));
        try (Connection con = new ConexionBD(url()).obtenerConexion()) {
            assertNotNull(con);
            assertFalse(con.isClosed());
        }
    }

    @Test
    void creaLaCarpetaSiNoExiste() throws Exception {
        assertFalse(Files.exists(carpeta.resolve("datos")));
        try (Connection con = new ConexionBD(url()).obtenerConexion()) {
            assertNotNull(con);
            assertFalse(con.isClosed());
        }
        assertTrue(Files.isDirectory(carpeta.resolve("datos")));
    }

    @Test
    void inicializarCreaLasDosTablas() throws Exception {
        new ConexionBD(url()).inicializar();
        assertEquals(2, contar("SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name IN ('plataforma', 'videojuego')"));
    }

    @Test
    void inicializarDosVecesNoBorraLosDatos() throws Exception {
        ConexionBD conexion = new ConexionBD(url());
        conexion.inicializar();
        try (Connection con = DriverManager.getConnection(url())) {
            con.createStatement().executeUpdate("INSERT INTO plataforma VALUES ('PS5', 'PlayStation 5', 'Sony', 2020)");
        }
        assertDoesNotThrow(conexion::inicializar);
        assertEquals(1, contar("SELECT COUNT(*) FROM plataforma"));
    }

    @Test
    void rutaImposibleLanzaAplicationException() throws Exception {
        Path ficheroNormal = Files.createFile(carpeta.resolve("no-soy-carpeta"));
        String urlImposible = "jdbc:sqlite:" + ficheroNormal.resolve("arcade.sqlite3").toAbsolutePath();
        assertThrows(AplicationException.class, () -> new ConexionBD(urlImposible).obtenerConexion());
    }
}
