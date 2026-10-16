package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Vehiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehiculoDao extends ConexionBD {
    //private  ConexionBD conexionBD;

    public VehiculoDao(String url) {
        super(url);
    }

    public List<Vehiculo> listar() {

        List<Vehiculo> vehiculos = new ArrayList<>();

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT matricula ,marca ,modelo ,anio FROM Vehiculos ORDER BY matricula");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                vehiculos.add(map(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error listando todos los vehiculos", e);
        }

        return vehiculos;
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE matricula = ?");
            preparedStatement.setString(1, matricula);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return Optional.of(map(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error buscando vehiculo por matricula", e);
        }
        return Optional.empty();
    }

    public boolean insertar(Vehiculo vehiculo) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Vehiculos (matricula, marca, modelo, anio) VALUES (?,?,?,?)");
            preparedStatement.setString(1, vehiculo.getMatricula());
            preparedStatement.setString(2, vehiculo.getMarca());
            preparedStatement.setString(3, vehiculo.getModelo());
            preparedStatement.setInt(4, vehiculo.getAnio());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error insertando vehiculo", e);
        }
    }

    public boolean actualizar(Vehiculo vehiculo) {

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ? WHERE matricula = ?");
            preparedStatement.setString(1, vehiculo.getMarca());
            preparedStatement.setString(2, vehiculo.getModelo());
            preparedStatement.setInt(3, vehiculo.getAnio());
            preparedStatement.setString(4, vehiculo.getMatricula());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error actualizando el vehiculo", e);
        }
    }

    public boolean eliminar(String matricula) {
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Vehiculos WHERE matricula = ?");
            preparedStatement.setString(1, matricula);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new AplicationException("Error elimintando el vehiculo", e);
        }
    }

    public List<Vehiculo> buscarPorMarca(String texto) {

        List<Vehiculo> vehiculos = new ArrayList<>();

        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca LIKE ?");
            preparedStatement.setString(1, "%" + texto + "%");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                vehiculos.add(map(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error buscando vehiculo por marca ", e);
        }

        return vehiculos;

    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        List<Vehiculo> vehiculosPorAnio = new ArrayList<>();
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE anio >= ? AND anio <= ?");
            preparedStatement.setInt(1, desde);
            preparedStatement.setInt(2, hasta);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                vehiculosPorAnio.add(map(rs));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error buscando vehiculo por anio", e);
        }
        return vehiculosPorAnio;
    }

    public int contar() {
        try (Connection connection = this.obtenerConexion()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM Vehiculos");
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new AplicationException("Error contando vehiculos", e);
        }

        return 0;
    }

    public Map<String, Integer> contarPorMarca() {

        Map<String, Integer> contadosPorMarca = new LinkedHashMap<>();

        try (Connection connection = this.obtenerConexion()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT marca, COUNT(marca) AS total FROM Vehiculos GROUP BY marca");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                contadosPorMarca.put(rs.getString("marca"),rs.getInt("total"));
            }

        } catch (SQLException e) {
            throw new AplicationException("Error contando los vehiculos por marca" ,e);
        }

        return contadosPorMarca;
    }

    public Vehiculo map(ResultSet rs) throws SQLException {
        return new Vehiculo(rs.getString("matricula"), rs.getString("marca"), rs.getString("modelo"), rs.getInt("anio"));
    }
}
