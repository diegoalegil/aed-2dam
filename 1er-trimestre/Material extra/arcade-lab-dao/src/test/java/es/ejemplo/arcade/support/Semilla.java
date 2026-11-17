package es.ejemplo.arcade.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Datos iniciales de las pruebas, insertados con JDBC propio (no usa las clases que se evalúan).
 * Las plataformas se insertan a propósito en un orden que NO es el alfabético.
 */
public final class Semilla {

    private Semilla() {
    }

    public static void cargar(String url) {
        try (Connection c = DriverManager.getConnection(url)) {
            plataformas(c);
            videojuegos(c);
        } catch (SQLException e) {
            throw new IllegalStateException("Error cargando la semilla de pruebas: " + e.getMessage(), e);
        }
    }

    private static void plataformas(Connection c) throws SQLException {
        String sql = "INSERT INTO plataforma (codigo, nombre, fabricante, anio) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            plataforma(ps, "PS5", "PlayStation 5", "Sony", 2020);
            plataforma(ps, "SWITCH", "Nintendo Switch", "Nintendo", 2017);
            plataforma(ps, "PC", "PC", "Varios", 1981);
            plataforma(ps, "XSX", "Xbox Series X", "Microsoft", 2020);
        }
    }

    private static void plataforma(PreparedStatement ps, String codigo, String nombre,
                                   String fabricante, int anio) throws SQLException {
        ps.setString(1, codigo);
        ps.setString(2, nombre);
        ps.setString(3, fabricante);
        ps.setInt(4, anio);
        ps.executeUpdate();
    }

    private static void videojuegos(Connection c) throws SQLException {
        String sql = "INSERT INTO videojuego (id, titulo, precio, multijugador, nota, plataforma) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            videojuego(ps, 1, "Baldur's Gate 3", 59.99, 1, 96, "PC");
            videojuego(ps, 2, "Mario Kart 8 Deluxe", 49.99, 1, 92, "SWITCH");
            videojuego(ps, 3, "Astro Bot", 39.99, 0, 94, "PS5");
            videojuego(ps, 4, "Hollow Knight: Silksong", 19.99, 0, 88, "SWITCH");
            videojuego(ps, 5, "Assassin's Creed Shadows", 69.99, 0, 81, "PS5");
            videojuego(ps, 6, "Fortnite", 0.00, 1, 72, "PC");
            videojuego(ps, 7, "Hades II", 39.99, 0, 90, "PC");
        }
    }

    private static void videojuego(PreparedStatement ps, int id, String titulo, double precio,
                                   int multijugador, int nota, String plataforma) throws SQLException {
        ps.setInt(1, id);
        ps.setString(2, titulo);
        ps.setDouble(3, precio);
        ps.setInt(4, multijugador);
        ps.setInt(5, nota);
        ps.setString(6, plataforma);
        ps.executeUpdate();
    }
}
