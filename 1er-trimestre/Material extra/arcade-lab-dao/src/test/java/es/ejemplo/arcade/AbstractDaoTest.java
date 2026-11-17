package es.ejemplo.arcade;

import es.ejemplo.arcade.config.ConexionBD;
import es.ejemplo.arcade.dao.AbstractDao;
import es.ejemplo.arcade.exception.AplicationException;
import es.ejemplo.arcade.support.BaseDatosTest;
import es.ejemplo.arcade.support.Semilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AbstractDao se prueba con DAO hechos solo para el test: así no depende de PlataformaDao ni de VideojuegoDao.
 */
class AbstractDaoTest extends BaseDatosTest {

    /** DAO de prueba: cada fila se convierte en su título. */
    static class TitulosDao extends AbstractDao<String> {
        TitulosDao(String url) { super(url); }

        @Override
        protected String mapRsToClass(ResultSet rs) throws SQLException {
            return rs.getString("titulo");
        }
    }

    /** DAO de prueba con otro mapeo: el título en mayúsculas. */
    static class TitulosMayusculasDao extends AbstractDao<String> {
        TitulosMayusculasDao(String url) { super(url); }

        @Override
        protected String mapRsToClass(ResultSet rs) throws SQLException {
            return rs.getString("titulo").toUpperCase();
        }
    }

    private TitulosDao dao;

    @BeforeEach
    void prepararDao() {
        Semilla.cargar(url);
        dao = new TitulosDao(url);
    }

    @Test
    void heredaDeConexionBD() {
        assertEquals(ConexionBD.class, AbstractDao.class.getSuperclass(), "AbstractDao tiene que heredar de ConexionBD");
    }

    @Test
    void consultarDevuelveLasFilasEnElOrdenDelSelect() throws SQLException {
        try (Connection c = new ConexionBD(url).obtenerConexion();
             PreparedStatement ps = c.prepareStatement("SELECT titulo FROM videojuego ORDER BY id")) {
            assertEquals(List.of("Baldur's Gate 3", "Mario Kart 8 Deluxe", "Astro Bot", "Hollow Knight: Silksong",
                    "Assassin's Creed Shadows", "Fortnite", "Hades II"), dao.consultar(ps));
        }
    }

    @Test
    void consultarSinFilasDevuelveListaVacia() throws SQLException {
        try (Connection c = new ConexionBD(url).obtenerConexion();
             PreparedStatement ps = c.prepareStatement("SELECT titulo FROM videojuego WHERE precio > 1000")) {
            List<String> resultado = dao.consultar(ps);
            assertNotNull(resultado, "consultar nunca devuelve null");
            assertEquals(List.of(), resultado);
        }
    }

    @Test
    void consultarUsaLosParametrosQueYaTieneElPreparedStatement() throws SQLException {
        try (Connection c = new ConexionBD(url).obtenerConexion();
             PreparedStatement ps = c.prepareStatement("SELECT titulo FROM videojuego WHERE plataforma = ? ORDER BY id")) {
            ps.setString(1, "PC");
            assertEquals(List.of("Baldur's Gate 3", "Fortnite", "Hades II"), dao.consultar(ps));
        }
    }

    @Test
    void consultarUsaElMapeoDeLaClaseHija() throws SQLException {
        TitulosMayusculasDao mayusculas = new TitulosMayusculasDao(url);
        try (Connection c = new ConexionBD(url).obtenerConexion();
             PreparedStatement ps = c.prepareStatement("SELECT titulo FROM videojuego WHERE plataforma = ? ORDER BY id")) {
            ps.setString(1, "PS5");
            assertEquals(List.of("ASTRO BOT", "ASSASSIN'S CREED SHADOWS"), mayusculas.consultar(ps));
        }
    }

    @Test
    void actualizarDevuelveLasFilasModificadas() throws SQLException {
        try (Connection c = new ConexionBD(url).obtenerConexion();
             PreparedStatement ps = c.prepareStatement("UPDATE videojuego SET nota = 50 WHERE plataforma = 'SWITCH'")) {
            assertEquals(2, dao.actualizar(ps));
        }
        assertEquals(2, contar("SELECT COUNT(*) FROM videojuego WHERE nota = 50"));
        try (Connection c = new ConexionBD(url).obtenerConexion();
             PreparedStatement ps = c.prepareStatement("UPDATE videojuego SET nota = 0 WHERE plataforma = 'XSX'")) {
            assertEquals(0, dao.actualizar(ps), "si no coincide ninguna fila, 0");
        }
    }

    @Test
    void actualizarConInsertDevuelveUnoYLaFilaSePuedeLeer() throws SQLException {
        try (Connection c = new ConexionBD(url).obtenerConexion();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO videojuego (id, titulo, precio, multijugador, nota, plataforma) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, 8);
            ps.setString(2, "Tom Clancy's Rainbow Six Siege");
            ps.setDouble(3, 19.99);
            ps.setBoolean(4, true);
            ps.setInt(5, 85);
            ps.setString(6, "PC");
            assertEquals(1, dao.actualizar(ps));
        }
        try (Connection c = new ConexionBD(url).obtenerConexion();
             PreparedStatement ps = c.prepareStatement("SELECT titulo FROM videojuego WHERE id = ?")) {
            ps.setInt(1, 8);
            assertEquals(List.of("Tom Clancy's Rainbow Six Siege"), dao.consultar(ps));
        }
    }

    @Test
    void consultarConSentenciaCerradaLanzaAplicationException() throws SQLException {
        try (Connection c = new ConexionBD(url).obtenerConexion()) {
            PreparedStatement ps = c.prepareStatement("SELECT titulo FROM videojuego");
            ps.close();
            assertThrows(AplicationException.class, () -> dao.consultar(ps));
        }
    }

    @Test
    void actualizarConSentenciaCerradaLanzaAplicationException() throws SQLException {
        try (Connection c = new ConexionBD(url).obtenerConexion()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM videojuego");
            ps.close();
            assertThrows(AplicationException.class, () -> dao.actualizar(ps));
        }
        assertEquals(7, contar("SELECT COUNT(*) FROM videojuego"));
    }

    @Test
    void actualizarConClaveRepetidaLanzaAplicationException() throws SQLException {
        try (Connection c = new ConexionBD(url).obtenerConexion();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO plataforma (codigo, nombre, fabricante, anio) VALUES ('PS5', 'Otra', 'Otro', 2000)")) {
            assertThrows(AplicationException.class, () -> dao.actualizar(ps));
        }
        assertEquals("PlayStation 5", leerTexto("SELECT nombre FROM plataforma WHERE codigo = 'PS5'"));
    }
}
