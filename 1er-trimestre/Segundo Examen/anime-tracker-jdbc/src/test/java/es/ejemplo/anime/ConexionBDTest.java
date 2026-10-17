package es.ejemplo.anime;

import es.ejemplo.anime.config.ConexionBD;
import es.ejemplo.anime.exception.AplicationException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConexionBDTest extends BaseDatosTest {

    private int contar(String sql) throws Exception {
        try (Connection con = DriverManager.getConnection(url);
             ResultSet rs = con.createStatement().executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    @Test @Order(1)
    void conectaSiLaCarpetaYaExiste() throws Exception {
        Files.createDirectories(carpetaTemporal.resolve("datos"));
        try (Connection con = new ConexionBD(url).obtenerConexion()) {
            assertNotNull(con);
            assertFalse(con.isClosed());
        }
    }

    @Test @Order(2)
    void creaLaCarpetaSiNoExiste() throws Exception {
        assertFalse(Files.exists(carpetaTemporal.resolve("datos")));
        try (Connection con = new ConexionBD(url).obtenerConexion()) {
            assertNotNull(con);
            assertFalse(con.isClosed());
        }
        assertTrue(Files.isDirectory(carpetaTemporal.resolve("datos")));
    }

    @Test @Order(3)
    void inicializarCreaLaTablaSeries() throws Exception {
        new ConexionBD(url).inicializar();
        assertEquals(1, contar("SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = 'series'"));
    }

    @Test @Order(4)
    void inicializarDosVecesNoBorraLosDatos() throws Exception {
        ConexionBD conexion = new ConexionBD(url);
        conexion.inicializar();
        try (Connection con = DriverManager.getConnection(url)) {
            con.createStatement().executeUpdate(
                "INSERT INTO series VALUES ('AOT-001', 'Shingeki no Kyojin', 'shonen', 87, 9.0)");
        }
        assertDoesNotThrow(conexion::inicializar);
        assertEquals(1, contar("SELECT COUNT(*) FROM series"));
    }

    @Test @Order(5)
    void rutaImposibleLanzaAplicationException() throws Exception {
        Path ficheroNormal = Files.createFile(carpetaTemporal.resolve("no-soy-carpeta"));
        String urlImposible = "jdbc:sqlite:" + ficheroNormal.resolve("anime.sqlite3").toAbsolutePath();
        assertThrows(AplicationException.class, () -> new ConexionBD(urlImposible).obtenerConexion());
    }
}
