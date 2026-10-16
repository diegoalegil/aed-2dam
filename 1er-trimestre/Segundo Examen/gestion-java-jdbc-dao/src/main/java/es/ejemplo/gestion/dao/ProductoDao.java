package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProductoDao extends ConexionBD {

    //private ConexionBD conexionBD;

    public ProductoDao(String url) {
        super(url);
    }

    public List<Producto> listar() {

        List<Producto> productos = new ArrayList<>();

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id ,nombre ,precio FROM productos ORDER BY id");

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Producto producto = map(rs);
                productos.add(producto);
            }

        } catch (SQLException e) {
            throw new AplicationException("No ha sido posible listar la base de datos", e);
        }

        return productos;
    }

    public Optional<Producto> buscarPorId(int id) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id ,nombre ,precio FROM productos WHERE id = ? ");

            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return Optional.of(map(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error buscando por id", e);
        }

        return Optional.empty();
    }

    public boolean insertar(Producto producto) {

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO productos (id ,nombre ,precio) VALUES (? ,? ,?)");

            preparedStatement.setInt(1, producto.getId());
            preparedStatement.setString(2, producto.getNombre());
            preparedStatement.setDouble(3, producto.getPrecio());

            return preparedStatement.executeUpdate() > 0;


        } catch (SQLException e) {
            throw new AplicationException("Se ha producido un error insertando el producto", e);
        }
    }

    public boolean actualizar(Producto producto) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Productos SET nombre = ?, precio = ? WHERE id = ?");

            preparedStatement.setString(1, producto.getNombre());
            preparedStatement.setDouble(2, producto.getPrecio());
            preparedStatement.setInt(3, producto.getId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error actualizando los datos", e);
        }
    }

    public boolean eliminar(int id) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Productos WHERE id = ?");
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("No ha sido posible eliminar los datos", e);
        }
    }

    public List<Producto> buscarPorNombre(String texto) {

        List<Producto> productosPorNombre = new ArrayList<>();

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id ,nombre ,precio FROM Productos WHERE nombre LIKE ? ORDER BY  nombre");
            preparedStatement.setString(1, "%" + texto + "%");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                productosPorNombre.add(map(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("No ha sido posible encontrarlo por nombre", e);
        }
        return productosPorNombre;
    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {

        List<Producto> productosPorPrecio = new ArrayList<>();

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, nombre,precio FROM Productos WHERE precio >= ? AND precio <= ? ");
            preparedStatement.setDouble(1, minimo);
            preparedStatement.setDouble(2, maximo);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                productosPorPrecio.add(map(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error buscando los productos por precio", e);
        }

        return productosPorPrecio;
    }

    public int contar() {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM Productos");
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new AplicationException("Error contando los productos", e);
        }

        return 0;
    }

    public double precioMedio() {

        try (Connection connection = this.obtenerConexion();) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT AVG(precio) FROM Productos");
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            throw new AplicationException("Error calculando el precio medio", e);
        }

        return 0;
    }

    private Producto map(ResultSet rs) throws SQLException {
        return new Producto(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio"));
    }
}
