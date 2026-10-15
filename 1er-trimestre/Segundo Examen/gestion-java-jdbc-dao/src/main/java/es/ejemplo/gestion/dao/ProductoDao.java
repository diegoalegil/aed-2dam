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

    public ProductoDao(String url)  {
        super(url);
    }

    public List<Producto> listar() {

        List<Producto> lista = new ArrayList<>();
        try (Connection connection = this.obtenerConexion();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, nombre, precio FROM Productos ORDER BY id");
            ResultSet rs = preparedStatement.executeQuery()){

            while (rs.next()) {
                lista.add(mapProducto(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("Ha habido un error al listar los Productos",e);
        }
        //conexion
        //sql
        //preparateStatement
        //ResultSet rs
        //rs.next()
        //while(rs.next()) {
        //
        return lista;
    }

    public Optional<Producto> buscarPorId(int id) {



        return null;
    }

    public boolean insertar(Producto producto) {
        return false;
    }

    public boolean actualizar(Producto producto) {
        return false;
    }

    public boolean eliminar(int id) {
        return false;
    }

    public List<Producto> buscarPorNombre(String texto) {
        return null;
    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        return null;
    }

    public int contar() {
        return 0;
    }

    public double precioMedio() {
        return 0;
    }

    private Producto mapProducto(ResultSet rs) throws SQLException {
        return new Producto(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio"));
    }
}

