package es.ejemplo.arcade.support;

import es.ejemplo.arcade.config.ConexionBD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Cada test trabaja con una base nueva dentro de una carpeta temporal.
 * La carpeta "datos" NO existe al empezar: la crea ConexionBD al inicializar.
 * Los métodos de ayuda usan JDBC propio (no usan las clases que se evalúan).
 */
public abstract class BaseDatosTest {
    @TempDir
    protected Path carpeta;
    protected String url;

    @BeforeEach
    protected void prepararBaseDatos() {
        url = "jdbc:sqlite:" + carpeta.resolve("datos").resolve("arcade.sqlite3").toAbsolutePath();
        new ConexionBD(url).inicializar();
    }

    /** Ejecuta una sentencia INSERT, UPDATE o DELETE directamente sobre la base. */
    protected void ejecutar(String sql) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error en la sentencia de prueba: " + sql, e);
        }
    }

    /** Devuelve el número de la primera columna de la primera fila (por ejemplo, un COUNT(*)). */
    protected int contar(String sql) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error en la consulta de prueba: " + sql, e);
        }
    }

    /** Devuelve el decimal de la primera columna de la primera fila. */
    protected double leerDecimal(String sql) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : Double.NaN;
        } catch (SQLException e) {
            throw new IllegalStateException("Error en la consulta de prueba: " + sql, e);
        }
    }

    /** Devuelve el texto de la primera columna de la primera fila, o null si no hay filas. */
    protected String leerTexto(String sql) {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        } catch (SQLException e) {
            throw new IllegalStateException("Error en la consulta de prueba: " + sql, e);
        }
    }
}
